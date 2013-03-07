package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import abbot.tester.swt.TreeItemTester;
import abbot.tester.swt.TreeTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator2;
import com.windowtester.test.locator.swt.shells.TreeItemTestShell;


/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Frederic Gurr - initial API and implementation
 *******************************************************************************/

public class TreeItemLocator2Test extends AbstractLocatorTest {

	TreeItemTestShell window;
	
	 
	@Override
	public void uiSetup() {
		window = new TreeItemTestShell();
		window.open();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public TreeItemTestShell getWindow() {
		return window;
	}
	
	public void testSingleTreeItems() throws WidgetSearchException {
		checkSingleTreeItem("Root/label2", "label2");
		checkSingleTreeItem("Root/label3/label6", "label6");
	}

	//TODO: testNoWidgetFoundException
	//TODO: testSingleTreeItemWithIndex
	
	private void checkSingleTreeItem(String fullPath, String itemText) throws WidgetSearchException {
		IUIContext ui = getUI();
//		TreeItemLocator treeItemLocator = new TreeItemLocator(fullPath, new WidgetReference<Tree>(getWindow().tree));
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2(fullPath, new WidgetReference<Tree>(getWindow().tree));
		ui.click(treeItemLocator);
		ui.assertThat(treeItemLocator.isSelected());
		assertEquals(1, new TreeTester().getSelection(getWindow().tree).length); //assert that only one tree item is selected
		ui.assertThat(treeItemLocator.hasText(itemText));
	}

	//TODO: test also nested items (test the error message!)
	public void testMultipleTreeItemsWithoutIndex() throws WidgetSearchException {
		IUIContext ui = getUI();
		
		try{
//			ui.click(new TreeItemLocator("Root/label1", new WidgetReference<Tree>(getWindow().tree)));
			ui.click(new TreeItemLocator2("Root/label1", new WidgetReference<Tree>(getWindow().tree)));
			fail("Should throw a MultipleWidgetsFoundException");
		}catch(MultipleWidgetsFoundException e){
			//show expected exception
			System.out.println(e);
		}
	}

	//TODO: test expanded status (how many tree items were "touched")
	public void testMultipleTreeItemsWithIndex() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1", "label1", 0);
		checkMultipleTreeItems("Root/label1", "label1", 1);
		checkMultipleTreeItems("Root/label1", "label1", 2);

		checkMultipleTreeItems("Root/label1/label4", "label4", 0);
		checkMultipleTreeItems("Root/label1/label4", "label4", 1);
		checkMultipleTreeItems("Root/label1/label4", "label4", 2);

		checkMultipleTreeItems("Root/label1/label4/label5", "label5", 0);
		//TODO: test wrong index
	}

	private void checkMultipleTreeItems(String fullPath, String itemText, int index) throws WidgetSearchException {
		IUIContext ui = getUI();
//		TreeItemLocator treeItemLocator = new TreeItemLocator(fullPath, index, new WidgetReference<Tree>(getWindow().tree));
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2(fullPath, index, new WidgetReference<Tree>(getWindow().tree));
		ui.click(treeItemLocator);
		ui.assertThat(treeItemLocator.isSelected());
		assertEquals(1, new TreeTester().getSelection(getWindow().tree).length); //assert that only one tree item is selected
		ui.assertThat(treeItemLocator.hasText(itemText));
	}
	
	//TODO: which tests have something to do with TreeItemLocator and need to be run?
	//TODO: test dynamic trees
	//TODO: test pattern matching
	
	//TODO: test tree with colums
	//TODO: implement "column filters", find treeItem by specifying column contents
	//TODO: implement and test code to record the correct TreeItemLocator parameters
	

}