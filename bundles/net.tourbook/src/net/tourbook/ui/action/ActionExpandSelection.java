/*******************************************************************************
 * Copyright (C) 2005, 2014  Wolfgang Schramm and Contributors
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 *******************************************************************************/
package net.tourbook.ui.action;

import java.util.Iterator;

import net.tourbook.Messages;
import net.tourbook.application.TourbookPlugin;
import net.tourbook.common.util.ITourViewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

public class ActionExpandSelection extends Action {

	private ITourViewer	_tourViewer;

	private int			_expandLevels	= 2;

	public ActionExpandSelection(final ITourViewer tourViewer) {

		super(null, AS_PUSH_BUTTON);

		_tourViewer = tourViewer;

		setText(Messages.app_action_expand_selection_tooltip);

		setToolTipText(Messages.app_action_expand_selection_tooltip);
		setImageDescriptor(TourbookPlugin.getImageDescriptor(Messages.Image__expand_all));
	}

	/**
	 * @param tourViewer
	 * @param expandLevels
	 *            Number of tree levels which should be expanded, default is 2.
	 *            <p>
	 *            All levels can be expanded with {@link TreeViewer#ALL_LEVELS}.
	 */
	public ActionExpandSelection(final ITourViewer tourViewer, final int expandLevels) {

		this(tourViewer);

		_expandLevels = expandLevels;
	}

	@Override
	public void run() {

		if (_tourViewer == null) {
			return;
		}

		final ColumnViewer viewer = _tourViewer.getViewer();
		if (viewer instanceof TreeViewer) {

			final TreeViewer treeViewer = (TreeViewer) viewer;
			final ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();

			if (selection.size() == 0) {
				return;
			}

			final Tree tree = treeViewer.getTree();
			tree.setRedraw(false);
			{
				for (final Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
					treeViewer.expandToLevel(iterator.next(), _expandLevels);
				}
			}
			tree.setRedraw(true);
		}
	}
}
