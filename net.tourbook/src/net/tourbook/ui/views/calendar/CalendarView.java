package net.tourbook.ui.views.calendar;

import java.util.ArrayList;

import net.tourbook.application.TourbookPlugin;
import net.tourbook.chart.Activator;
import net.tourbook.data.TourData;
import net.tourbook.preferences.ITourbookPreferences;
import net.tourbook.tour.ITourEventListener;
import net.tourbook.tour.SelectionDeletedTours;
import net.tourbook.tour.SelectionTourId;
import net.tourbook.tour.TourEventId;
import net.tourbook.tour.TourManager;
import net.tourbook.ui.ITourProvider;
import net.tourbook.ui.views.calendar.CalendarGraph.NavigationStyle;
import net.tourbook.ui.views.calendar.CalendarGraph.Type;
import net.tourbook.util.SelectionProvider;
import net.tourbook.util.Util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.joda.time.DateTime;

public class CalendarView extends ViewPart implements ITourProvider{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String					ID					= "net.tourbook.views.calendar.CalendarView";
	private final IPreferenceStore				_prefStore			= TourbookPlugin.getDefault().getPreferenceStore();
	private final IDialogSettings				_state				= TourbookPlugin
																			.getDefault()
																			.getDialogSettingsSection(
																					"TourCalendarView");				//$NON-NLS-1$

	private String								STATE_SELECTED_TOURS	= "SelectedTours";								// $NON-NLS-1$
	private String								STATE_FIRST_DAY			= "FirstDayDisplayed";							// $NON-NLS-1$
	private String								STATE_NUM_OF_WEEKS		= "NumberOfWeeksDisplayed";					// $NON-NLS-1$
	private String								STATE_IS_LINKED			= "Linked";									// $NON-NLS-1$

	private Action								_forward, _back;
	private Action								_zoomIn, _zoomOut;
	private Action								_linked;
	private Action								_today;
	private Action								_setNavigationStylePhysical, _setNavigationStyleLogical;
	private Action								_setNumberOfToursPerDay0;
	private Action								_setNumberOfToursPerDay1;
	private Action								_setNumberOfToursPerDay2;
	private Action								_setNumberOfToursPerDay3;
	private Action								_setNumberOfToursPerDay4;
	private Action								_setTourSizeDynamic;

	private PageBook							_pageBook;
	private CalendarComponents					_calendarComponents;
	private CalendarGraph						_calendarGraph;

	private ISelectionProvider					_selectionProvider;
	private ISelectionListener					_selectionListener;
	private IPartListener2						_partListener;
	private IPropertyChangeListener				_prefChangeListener;
	private ITourEventListener					_tourPropertyListener;

	private CalendarYearMonthContributionItem	_cymci;

	public CalendarView() {}

	private void addPartListener() {

		_partListener = new IPartListener2() {
			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {}

			@Override
			public void partBroughtToTop(final IWorkbenchPartReference partRef) {}

			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false) == CalendarView.this) {
					saveState();
				}
			}

			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {}

			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {}

			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {}

			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {}

			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {}
		};
		getViewSite().getPage().addPartListener(_partListener);
	}

	private void addPrefListener() {

		_prefChangeListener = new IPropertyChangeListener() {

			public void propertyChange(final PropertyChangeEvent event) {

				final String property = event.getProperty();

				/*
				 * set a new chart configuration when the preferences has changed
				 */

				if (property.equals(ITourbookPreferences.APP_DATA_FILTER_IS_MODIFIED)) {

					refreshCalendar();

				} else if (property.equals(ITourbookPreferences.TOUR_TYPE_LIST_IS_MODIFIED)) {

					// update statistics
					refreshCalendar();

				}
			}

		};

		// add pref listener
		_prefStore.addPropertyChangeListener(_prefChangeListener);

	}

	// create and register our selection listener
	private void addSelectionListener() {

		_selectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {

				// prevent to listen to a selection which is originated by this year chart
				if (part == CalendarView.this) {
					return;
				}

				onSelectionChanged(selection);

			}
		};

		// register selection listener in the page
		getSite().getPage().addPostSelectionListener(_selectionListener);
	}

	// create and register our selection provider
	private void addSelectionProvider() {

		getSite().setSelectionProvider(_selectionProvider = new SelectionProvider());

		_calendarGraph.addSelectionProvider(new ICalendarSelectionProvider() {

			@Override
			public void selectionChanged(final Type type, final long id) {
				if (type == Type.TOUR) {
					_selectionProvider.setSelection(new SelectionTourId(id));
				}
			}

		});
	}

	private void addTourEventListener() {

		_tourPropertyListener = new ITourEventListener() {
			@Override
			public void tourChanged(final IWorkbenchPart part, final TourEventId eventId, final Object eventData) {

				if (eventId == TourEventId.TOUR_CHANGED || eventId == TourEventId.UPDATE_UI) {
					/*
					 * it is possible when a tour type was modified, the tour can be hidden or
					 * visible in the viewer because of the tour type filter
					 */
					refreshCalendar();

				} else if (eventId == TourEventId.TAG_STRUCTURE_CHANGED
						|| eventId == TourEventId.ALL_TOURS_ARE_MODIFIED) {

					refreshCalendar();
				}
			}
		};
		TourManager.getInstance().addTourEventListener(_tourPropertyListener);
	}
	
	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	@Override
	public void createPartControl(final Composite parent) {
		
		addPartListener();
		addPrefListener();
		addTourEventListener();

		createUI(parent);

		makeActions();
		contributeToActionBars();

		addSelectionListener();
		addSelectionProvider();

		restoreState();

		// restore selection
		onSelectionChanged(getSite().getWorkbenchWindow().getSelectionService().getSelection());

		// final Menu contextMenu = TourContextMenu.getInstance().createContextMenu(this, _calendarGraph);
		final Menu contextMenu = (new TourContextMenu()).createContextMenu(this, _calendarGraph, getLocalActions());

		_calendarGraph.setMenu(contextMenu);

	}

	private void createUI(final Composite parent) {
		
		_pageBook = new PageBook(parent, SWT.NONE);
		_calendarComponents = new CalendarComponents(_pageBook, SWT.NORMAL);
		_calendarGraph = _calendarComponents.getGraph();
		_pageBook.showPage(_calendarComponents);
	}
	@Override
	public void dispose() {

		TourManager.getInstance().removeTourEventListener(_tourPropertyListener);
		getSite().getPage().removePostSelectionListener(_selectionListener);
		_prefStore.removePropertyChangeListener(_prefChangeListener);

		super.dispose();
	}

	private void fillLocalPullDown(final IMenuManager manager) {
		manager.add(_setNavigationStylePhysical);
		manager.add(_setNavigationStyleLogical);
		manager.add(new Separator());
		manager.add(_setNumberOfToursPerDay0);
		manager.add(_setNumberOfToursPerDay1);
		manager.add(_setNumberOfToursPerDay2);
		manager.add(_setNumberOfToursPerDay3);
		manager.add(_setNumberOfToursPerDay4);
		manager.add(new Separator());
		manager.add(_setTourSizeDynamic);

	}

	private void fillLocalToolBar(final IToolBarManager manager) {
		_cymci = new CalendarYearMonthContributionItem(_calendarGraph);
		_calendarGraph.setYearMonthContributor(_cymci);
		manager.add(_cymci);
		manager.add(new Separator());
		// manager.add(_back);
		// manager.add(_forward);
		manager.add(_today);
		manager.add(new Separator());
		manager.add(_zoomIn);
		manager.add(_zoomOut);
		manager.add(new Separator());
		manager.add(_linked);
	}

	private ArrayList<Action> getLocalActions () {
		final ArrayList<Action> localActions = new ArrayList<Action>();
		localActions.add(_back);
		localActions.add(_today);
		localActions.add(_forward);
		return localActions;

	}@Override
	public ArrayList<TourData> getSelectedTours() {
		
		final ArrayList<TourData> selectedTourData = new ArrayList<TourData>();
		final ArrayList<Long> tourIdSet = new ArrayList<Long>();
		tourIdSet.add(_calendarGraph.getSelectionTourId());
		for (final Long tourId : tourIdSet) {
			if (tourId > 0) { // < 0 means not selected
				selectedTourData.add(TourManager.getInstance().getTourData(tourId));
			}
		}
		return selectedTourData;
	}
			
	private void makeActions() {

		_back = new Action() {
			@Override
			public void run() {
				_calendarGraph.gotoPrevScreen();
			}
		};
		_back.setId("net.tourbook.calendar.back");
		_back.setText("Back");
		_back.setToolTipText("Back one screen");
		_back.setImageDescriptor(Activator.imageDescriptorFromPlugin("net.tourbook", "icons/arrow-down.png"));

		_forward = new Action() {
			@Override
			public void run() {
				_calendarGraph.gotoNextScreen();
			}
		};
		_forward.setText("Forward");
		_forward.setToolTipText("Forward one screen");
		_forward.setImageDescriptor(Activator.imageDescriptorFromPlugin("net.tourbook", "icons/arrow-up.png"));

		_zoomOut = new Action() {
			@Override
			public void run() {
				_calendarGraph.zoomOut();
			}
		};
		_zoomOut.setText("Zoom out");
		_zoomOut.setToolTipText("Show more weeks");
		_zoomOut.setImageDescriptor(Activator.imageDescriptorFromPlugin("net.tourbook", "icons/zoom-out.gif"));

		_zoomIn = new Action() {
			@Override
			public void run() {
				_calendarGraph.zoomIn();
			}
		};
		_zoomIn.setText("Zoom in");
		_zoomIn.setToolTipText("Show less weeks");
		_zoomIn.setImageDescriptor(Activator.imageDescriptorFromPlugin("net.tourbook", "icons/zoom-in.gif"));

		_linked = new Action(null, org.eclipse.jface.action.Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				_calendarGraph.setLinked(_linked.isChecked());
			}
		};
		_linked.setText("Link with other views");
		_linked.setImageDescriptor(Activator.imageDescriptorFromPlugin("net.tourbook", "icons/synced.gif"));
		_linked.setChecked(true);

		_today = new Action() {
			@Override
			public void run() {
				_calendarGraph.gotoToday();
			}
		};
		_today.setText("Go to today");
		_today.setImageDescriptor(Activator.imageDescriptorFromPlugin("net.tourbook", "icons/zoom-centered.png"));

		_setNavigationStylePhysical = new Action(null, org.eclipse.jface.action.Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				_setNavigationStyleLogical.setChecked(false);
				_calendarGraph.setNavigationStyle(NavigationStyle.PHYSICAL);
			}
		};
		_setNavigationStylePhysical.setText("Physical arrow key navigation");
		_setNavigationStylePhysical.setChecked(true);

		_setNavigationStyleLogical = new Action(null, org.eclipse.jface.action.Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				_setNavigationStylePhysical.setChecked(false);
				_calendarGraph.setNavigationStyle(NavigationStyle.LOGICAL);
			}
		};
		_setNavigationStyleLogical.setText("Logical arrow key navigation");
		_setNavigationStyleLogical.setChecked(false);

		_setNumberOfToursPerDay0 = new Action(null, org.eclipse.jface.action.Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				_calendarGraph.setNumberOfToursPerDay(0);
				_setNumberOfToursPerDay1.setChecked(false);
				_setNumberOfToursPerDay2.setChecked(false);
				_setNumberOfToursPerDay3.setChecked(false);
				_setNumberOfToursPerDay4.setChecked(false);
			}
		};
		_setNumberOfToursPerDay0.setText("All exercises max. size");
		_setNumberOfToursPerDay1 = new Action(null, org.eclipse.jface.action.Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				_calendarGraph.setNumberOfToursPerDay(1);
				_setNumberOfToursPerDay0.setChecked(false);
				_setNumberOfToursPerDay2.setChecked(false);
				_setNumberOfToursPerDay3.setChecked(false);
				_setNumberOfToursPerDay4.setChecked(false);
			}
		};
		_setNumberOfToursPerDay1.setText("Default 1 exercise per day");
		_setNumberOfToursPerDay2 = new Action(null, org.eclipse.jface.action.Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				_calendarGraph.setNumberOfToursPerDay(2);
				_setNumberOfToursPerDay0.setChecked(false);
				_setNumberOfToursPerDay1.setChecked(false);
				_setNumberOfToursPerDay3.setChecked(false);
				_setNumberOfToursPerDay4.setChecked(false);
			}
		};
		_setNumberOfToursPerDay2.setText("Default 2 exercises per day");
		_setNumberOfToursPerDay3 = new Action(null, org.eclipse.jface.action.Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				_calendarGraph.setNumberOfToursPerDay(3);
				_setNumberOfToursPerDay0.setChecked(false);
				_setNumberOfToursPerDay1.setChecked(false);
				_setNumberOfToursPerDay2.setChecked(false);
				_setNumberOfToursPerDay4.setChecked(false);
			}
		};
		_setNumberOfToursPerDay3.setText("Default 3 exercises per day");
		_setNumberOfToursPerDay4 = new Action(null, org.eclipse.jface.action.Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				_calendarGraph.setNumberOfToursPerDay(4);
				_setNumberOfToursPerDay0.setChecked(false);
				_setNumberOfToursPerDay1.setChecked(false);
				_setNumberOfToursPerDay2.setChecked(false);
				_setNumberOfToursPerDay3.setChecked(false);
			}
		};
		_setNumberOfToursPerDay4.setText("Default 4 exercises per day");

		_setTourSizeDynamic = new Action(null, org.eclipse.jface.action.Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				_calendarGraph.setTourFieldSizeDynamic(this.isChecked());
			}
		};
		_setTourSizeDynamic.setText("Resize exersizes if more than default number");

	}

	private void onSelectionChanged(final ISelection selection) {

		// show and select the selected tour
		if (selection instanceof SelectionTourId) {
			final Long newTourId = ((SelectionTourId) selection).getTourId();
			final Long oldTourId = _calendarGraph.getSelectionTourId();
			if (newTourId != oldTourId) {
				if (_linked.isChecked()) {
					_calendarGraph.gotoTourId(newTourId);
				} else {
					_calendarGraph.removeSelection();
				}
			}
		} else if (selection instanceof SelectionDeletedTours) {
			_calendarGraph.refreshCalendar();
		}
	}

	private void refreshCalendar() {
		if (null != _calendarGraph) {
			_calendarGraph.refreshCalendar();
		}
	}
	
	private void restoreState() {

		final int numWeeksDisplayed = Util.getStateInt(_state, STATE_NUM_OF_WEEKS, 5);
		_calendarGraph.setZoom(numWeeksDisplayed);

		final Long dateTimeMillis = Util.getStateLong(_state, STATE_FIRST_DAY, (new DateTime()).getMillis());
		final DateTime firstDate = new DateTime(dateTimeMillis);
		_calendarGraph.setFirstDay(firstDate);
		
		final Long selectedTourId = Util.getStateLong(_state, STATE_SELECTED_TOURS, new Long(-1));
		_calendarGraph.setSelectionTourId(selectedTourId);

//		final String[] selectedTourIds = _state.getArray(STATE_SELECTED_TOURS);
//		_selectedTourIds.clear();
//
//		if (selectedTourIds != null) {
//			for (final String tourId : selectedTourIds) {
//				try {
//					_selectedTourIds.add(Long.valueOf(tourId));
//				} catch (final NumberFormatException e) {
//					// ignore
//				}
//			}
//		}

		_linked.setChecked(Util.getStateBoolean(_state, STATE_IS_LINKED, true));

	}

	private void saveState() {

		// save current date displayed
		_state.put(STATE_FIRST_DAY, _calendarGraph.getFirstDay().getMillis());

		// save number of weeks displayed
		_state.put(STATE_NUM_OF_WEEKS, _calendarGraph.getZoom());
		
		// convert tour id's into string
		// final ArrayList<String> selectedTourIds = new ArrayList<String>();
		// for (final Long tourId : _selectedTourIds) {
		// 	selectedTourIds.add(tourId.toString());
		// }
		// until now we only implement single tour selection
		_state.put(STATE_SELECTED_TOURS, _calendarGraph.getSelectionTourId());

		_state.put(STATE_IS_LINKED, _linked.isChecked());

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		_calendarComponents.setFocus();
	}

//	private void showMessage(final String message) {
//		MessageDialog.openInformation(_pageBook.getShell(), "%view_name_Calendar", message);
//	}

}