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
package net.tourbook.sign;

import net.tourbook.common.util.TreeViewerItem;

import org.eclipse.jface.viewers.TreeViewer;

public abstract class TVIPrefSignItem extends TreeViewerItem {

	private TreeViewer	_signViewer;

	public TVIPrefSignItem(final TreeViewer signViewer) {
		_signViewer = signViewer;
	}

	public TreeViewer getSignViewer() {
		return _signViewer;
	}

}