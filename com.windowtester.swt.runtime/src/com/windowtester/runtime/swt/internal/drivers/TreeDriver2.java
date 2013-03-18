/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.drivers;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReference;
import com.windowtester.runtime.swt.internal.widgets.TreeItemReferenceContainer;
import com.windowtester.runtime.swt.internal.widgets.TreeReference;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.StringComparator;

/**
 * Given a path, this class drives tree selection by interacting with tree and tree item
 * references. This class collects state and thus should be re-used.
 */
public class TreeDriver2
{
	/**
	 * Used internally by
	 * {@link #getNextTreeItem(TreeItemReferenceContainer, int, String)}
	 */
	private TreeItemReference[] items;

	/**
	 * Used internally by
	 * {@link #getNextTreeItem(TreeItemReferenceContainer, int, String)}
	 */
	private String[][] textForItems;

	/**
	 * Expand the items in the tree to show the specified tree item
	 * 
	 * @param tree the tree containing the tree item
	 * @param itemPath the slash separated path to the tree item
	 * @return the tree item revealed
	 */
	public TreeItemReference reveal(TreeReference tree, String itemPath) throws WidgetNotFoundException, MultipleWidgetsFoundException{
		return reveal(tree, itemPath, WidgetLocator.UNASSIGNED);
	}

	/**
	 * Expand the items in the tree to show the specified tree item
	 * 
	 * @param tree the tree containing the tree item
	 * @param itemPath the slash separated path to the tree item
	 * @param index
	 * @return the tree item revealed
	 */
	public TreeItemReference reveal(TreeReference tree, String itemPath, int index) throws WidgetNotFoundException, MultipleWidgetsFoundException{
		PathString path = new PathString(itemPath);
		//find all treeItems that match the path
		//try String equals first
		List<TreeItemReference> treeItemReferences = recursiveFind(tree, path, index, false);
		//only if that does not find anything, try again with pattern matching
		if(treeItemReferences.isEmpty()){
			System.out.println("try pattern matching");
			treeItemReferences = recursiveFind(tree, path, index, true);
		}
		
		if(treeItemReferences.isEmpty()){
			ScreenCapture.createScreenCapture();
			throw new WidgetNotFoundException("No tree items found for \'" + itemPath + ".");
		}else if(treeItemReferences.size() > 1){
			//use index or throw exception
			if(index != WidgetLocator.UNASSIGNED){
				try{
					return treeItemReferences.get(index);
				}catch(IndexOutOfBoundsException e){
					//is this better than throwing a IndexOutOfBoundsException?
					throw new WidgetNotFoundException("No tree items found for \'" + itemPath + " with index "+ index + ".");
				}
			}else{
				throw new MultipleWidgetsFoundException("Multiple tree items found for \'" + itemPath + ".");
			}
		}else /*if(treeItemReferences.size() == 1) */{
			return treeItemReferences.get(0);
		}
	}

	/**
	 * Execute the specified operation then find a tree item with the matching node text.
	 * 
	 * @param container the reference to the container of the tree items to be searched
	 * @param columnCount the number of columns in the tree (1 or greater)
	 * @param nodeText the text to be matched
	 * @return the matching tree item (not <code>null</code>)
	 */
	//TODO: why do we need columnCount in this method?! Is it possible, that the tree path does not always contain the first column?
	private List<TreeItemReference> getNextTreeItem(final TreeItemReferenceContainer container, final int columnCount, String nodeText) 
			throws WidgetNotFoundException, MultipleWidgetsFoundException {
		container.expand();
		container.getDisplayRef().execute(new VoidCallable() {
			public void call() throws Exception {
				items = container.getItems();
				textForItems = new String[items.length][columnCount];
				for (int row = 0; row < items.length; row++) {
					TreeItemReference item = items[row];
					String[] rowText = textForItems[row];
					for (int column = 0; column < columnCount; column++)
						rowText[column] = item.getText(column);
				}
			}
		});

		List<TreeItemReference> found = revealMatch(nodeText, false);
		if(found.isEmpty()){
			found = revealMatch(nodeText, true);
		}
		
		if(!found.isEmpty()){
			throw new WidgetNotFoundException("No tree items found for \'" + nodeText + "\' in " + getAllItemText(textForItems));
		}
		return found;
	}

	// Look for an exact match
	private List<TreeItemReference> revealMatch(String nodeText, boolean patternMatch) {

		List<TreeItemReference> found = new ArrayList<TreeItemReference>();
		for (int row = 0; row < textForItems.length; row++) {
			String[] rowTexts = textForItems[row];
			for (String text : rowTexts) {
				if (patternMatch ? StringComparator.matches(StringUtils.trimMenuText(text), nodeText) : nodeText.equals(text)) {
					found.add(items[row]);
				}
			}
		}
		return found;
	}

	//TODO: how can possible tree item matches before the actual "hit" be skipped during search?
	//TODO: columns
	//TODO: pattern match
	//TODO: handle dynamic trees
	private List<TreeItemReference> recursiveFind(TreeItemReferenceContainer container, PathString ps, int index, boolean patternMatch){
		List<TreeItemReference> foundTreeItems = new ArrayList<TreeItemReference>();
		//does this still work with dynamic trees?
		if(container.getItems().length > 0){
			container.expand();
		}
		TreeItemReference[] items = container.getItems();
		String expectedItemText = ps.next();
		for(int i = 0; i < items.length; i++){
			String actualItemText = items[i].getText();
			//Use either String equals method or pattern matching
			//TODO: is trimming needed for pattern matching?
			if(patternMatch ? StringComparator.matches(actualItemText, expectedItemText) : expectedItemText.equals(actualItemText)){
				System.out.println("NodeText equals " + items[i].getText());
				if(ps.hasNext()){
					//shortcut
					if(index != WidgetLocator.UNASSIGNED && foundTreeItems.size() == index+1){
						return foundTreeItems;
					}
					foundTreeItems.addAll(recursiveFind(items[i], ps, index, patternMatch));
					ps.last(); //important!
				}else{
					foundTreeItems.add(items[i]);
				}
			}
		}
		return foundTreeItems;
	}

	/**
	 * Answer all text used in the text comparison as a single string.
	 * 
	 * @param op the operation containing the matching text
	 * @return a string for inclusion in a {@link WidgetNotFoundException} or
	 *         {@link MultipleWidgetsFoundException} message
	 */
	private StringBuilder getAllItemText(String[][] itemTexts) {
		StringBuilder allItemText = new StringBuilder(128);
		for (String[] rowTexts : itemTexts) {
			String separator = "\n";
			for (String text : rowTexts) {
				allItemText.append(separator);
				allItemText.append(text);
				separator = " | ";
			}
		}
		return allItemText;
	}
}
