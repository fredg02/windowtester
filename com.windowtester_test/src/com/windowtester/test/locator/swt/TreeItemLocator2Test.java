package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.Tree;
import abbot.tester.swt.TreeTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.WidgetReference;
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
	IUIContext ui;

	@Override
	public void uiSetup() {
		window = new TreeItemTestShell();
		window.open();
		ui = getUI();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public TreeItemTestShell getWindow() {
		return window;
	}
	
	public void testSingleTreeItems0() throws WidgetSearchException {
		checkSingleTreeItem("Root/label2", "label2");
	}

	public void testSingleTreeItems1() throws WidgetSearchException {
		checkSingleTreeItem("Root/label3/label6", "label6");
	}

	public void testSingleTreeItems2() throws WidgetSearchException {
		checkSingleTreeItem("Root/label1/label4/label5", "label5");

		//test expanded status (how many tree items were "touched")

		//First "Root/label1" treeItem is expanded (is it possible to NOT expand it during search?)
		TreeItemLocator2 label1_0 = new TreeItemLocator2("Root/label1", 0, new WidgetReference<Tree>(getWindow().tree));
		System.out.println("label1 index 0 isExpanded: "+ label1_0.isExpanded(ui));
		
		//TODO: Third "Root/label1" treeItem should not be expanded (-> no shortcut yet, without jeopardizing MultipleWidgetsException)
//		TreeItemLocator2 label1_2 = new TreeItemLocator2("Root/label1", 2, new WidgetReference<Tree>(getWindow().tree));
//		System.out.println("label1 index 2 isExpanded: "+ label1_2.isExpanded(ui));
//		assertFalse(label1_2.isExpanded(ui));
	}

	private void checkSingleTreeItem(String fullPath, String itemText) throws WidgetSearchException {
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2(fullPath, new WidgetReference<Tree>(getWindow().tree));
		ui.click(treeItemLocator);
		ui.assertThat(treeItemLocator.isSelected());
		assertEquals(1, new TreeTester().getSelection(getWindow().tree).length); //assert that only one tree item is selected
		ui.assertThat(treeItemLocator.hasText(itemText));
	}

	public void testSingleTreeItem_WidgetNotFoundException() throws WidgetSearchException {
		String fullPath = "Root/label3/nonExistingLabel";
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2(fullPath, new WidgetReference<Tree>(getWindow().tree));
		try{
			ui.click(treeItemLocator);
			fail("WidgetNotFoundException expected.");
		}catch(WidgetSearchException e){
			//show expected exception
			System.out.println("Expected exception: "+ e);
			//check if label3 node is expanded
			TreeItemLocator2 treeItemLocatorParent = new TreeItemLocator2("Root/label3", new WidgetReference<Tree>(getWindow().tree));
			System.out.println("Root/label3 isExpanded: "+ treeItemLocatorParent.isExpanded(ui));
		}
	}

	public void testSingleTreeItemWithIndex() throws WidgetSearchException {
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2("Root/label2", 0, new WidgetReference<Tree>(getWindow().tree));
		ui.click(treeItemLocator);
		ui.assertThat(treeItemLocator.isSelected());
		assertEquals(1, new TreeTester().getSelection(getWindow().tree).length); //assert that only one tree item is selected
		ui.assertThat(treeItemLocator.hasText("label2"));
	}

	//TODO: What is the correct behavior?
	public void testSingleTreeItemWithWrongIndex() throws WidgetSearchException {
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2("Root/label2", 1, new WidgetReference<Tree>(getWindow().tree));
//		try{
			ui.click(treeItemLocator);
//			fail("Should throw a WidgetNotFoundException.");
//		}catch(WidgetSearchException e){
//			System.out.println(e);
//		}
	}

	//TODO: fix test
	public void testSingleTreeItemPatternMatching() throws WidgetSearchException {
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2("Root/.*/.*/label5", new WidgetReference<Tree>(getWindow().tree));
		ui.click(treeItemLocator);
		ui.assertThat(treeItemLocator.isSelected());
		assertEquals(1, new TreeTester().getSelection(getWindow().tree).length); //assert that only one tree item is selected
		ui.assertThat(treeItemLocator.hasText("label5"));
	}

	//TODO: test also nested items (test the error message!)
	public void testMultipleTreeItemsWithoutIndex() throws WidgetSearchException {
		try{
			ui.click(new TreeItemLocator2("Root/label1", new WidgetReference<Tree>(getWindow().tree)));
			fail("Should throw a MultipleWidgetsFoundException");
		}catch(MultipleWidgetsFoundException e){
			//show expected exception
			e.printStackTrace();
		}

		try{
			ui.click(new TreeItemLocator2("Root/label1/label4", new WidgetReference<Tree>(getWindow().tree)));
			fail("Should throw a MultipleWidgetsFoundException");
		}catch(MultipleWidgetsFoundException e){
			//show expected exception
			e.printStackTrace();
		}
	}

	public void testMultipleTreeItemsWithIndex0() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1", "label1", 0);
	}

	public void testMultipleTreeItemsWithIndex1() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1", "label1", 1);
	}

	public void testMultipleTreeItemsWithIndex2() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1", "label1", 2);
	}

	public void testMultipleTreeItemsWithIndex3() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1/label4", "label4", 0);

		//test expanded status (how many tree items were "touched")

		//Second "Root/label1" treeItem should not be expanded (-> shortcut)
		TreeItemLocator2 label1_1 = new TreeItemLocator2("Root/label1", 1, new WidgetReference<Tree>(getWindow().tree));
		System.out.println("label1 index 1 isExpanded: "+ label1_1.isExpanded(ui));
		assertFalse(label1_1.isExpanded(ui));
		
		//Third "Root/label1" treeItem should not be expanded (-> shortcut)
		TreeItemLocator2 label1_2 = new TreeItemLocator2("Root/label1", 2, new WidgetReference<Tree>(getWindow().tree));
		System.out.println("label1 index 2 isExpanded: "+ label1_2.isExpanded(ui));
		assertFalse(label1_2.isExpanded(ui));
	}

	public void testMultipleTreeItemsWithIndex4() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1/label4", "label4", 1);

		//test expanded status (how many tree items were "touched")

		//First "Root/label1" treeItem is expanded (is it possible to NOT expand it during search?)
		TreeItemLocator2 label1_0 = new TreeItemLocator2("Root/label1", 0, new WidgetReference<Tree>(getWindow().tree));
		System.out.println("label1 index 0 isExpanded: "+ label1_0.isExpanded(ui));
		
		//Third "Root/label1" treeItem should not be expanded (-> shortcut)
		TreeItemLocator2 label1_2 = new TreeItemLocator2("Root/label1", 2, new WidgetReference<Tree>(getWindow().tree));
		System.out.println("label1 index 2 isExpanded: "+ label1_2.isExpanded(ui));
		assertFalse(label1_2.isExpanded(ui));
	}

	public void testMultipleTreeItemsWithIndex5() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1/label4", "label4", 2);
	}
	
	public void testMultipleTreeItemsWithIndex6() throws WidgetSearchException {
		checkMultipleTreeItems("Root/label1/label4/label5", "label5", 0);

		//test expanded status (how many tree items were "touched")

		//First "Root/label1" treeItem is expanded (is it possible to NOT expand it during search?)
		TreeItemLocator2 label1_0 = new TreeItemLocator2("Root/label1", 0, new WidgetReference<Tree>(getWindow().tree));
		System.out.println("label1 index 0 isExpanded: "+ label1_0.isExpanded(ui));

		//Third "Root/label1" treeItem should not be expanded (-> shortcut)
		TreeItemLocator2 label1_2 = new TreeItemLocator2("Root/label1", 2, new WidgetReference<Tree>(getWindow().tree));
		System.out.println("label1 index 2 isExpanded: "+ label1_2.isExpanded(ui));
		assertFalse(label1_2.isExpanded(ui));
	}

	private void checkMultipleTreeItems(String fullPath, String itemText, int index) throws WidgetSearchException {
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2(fullPath, index, new WidgetReference<Tree>(getWindow().tree));
		ui.click(treeItemLocator);
		ui.assertThat(treeItemLocator.isSelected());
		assertEquals(1, new TreeTester().getSelection(getWindow().tree).length); //assert that only one tree item is selected
		ui.assertThat(treeItemLocator.hasText(itemText));
	}
	
	//TODO: why is pattern matching not used here?
	public void testMultipleTreeItemsWithWrongIndex() throws WidgetSearchException {
		TreeItemLocator2 treeItemLocator = new TreeItemLocator2("Root/label1/label4", 4, new WidgetReference<Tree>(getWindow().tree));
		try{
			ui.click(treeItemLocator);
			fail("Should throw a WidgetNotFoundException.");
		}catch(WidgetSearchException e){
			System.out.println("Expected exception: "+ e);
			//are the other nodes (index 0-2) expanded? (currently: yes!)
			TreeItemLocator2 label4_0 = new TreeItemLocator2("Root/label1/label4", 0, new WidgetReference<Tree>(getWindow().tree));
			System.out.println("label4 index 0 isExpanded: "+ label4_0.isExpanded(ui));
			TreeItemLocator2 label4_1 = new TreeItemLocator2("Root/label1/label4", 1, new WidgetReference<Tree>(getWindow().tree));
			System.out.println("label4 index 1 isExpanded: "+ label4_1.isExpanded(ui));
			TreeItemLocator2 label4_2 = new TreeItemLocator2("Root/label1/label4", 2, new WidgetReference<Tree>(getWindow().tree));
			System.out.println("label4 index 2 isExpanded: "+ label4_2.isExpanded(ui));
		}
	}

	public void testMultipleTreeItemsWithPatternMatching() throws WidgetSearchException {
		checkMultipleTreeItems("Root/.*/label4", "label4", 2);
	}

	//TODO: which tests use TreeItemLocator and need to be run?
	/*
	 * BasicRecorderSmokeTests
	 * EclipseRecorderSmokeTests
	 * MenuItemSelectionTest
	 * ShellMonitorSmokeTest
	 * 
	 * JavaProjectHelper
	 * SimpleProjectHelper
	 * WorkBenchHelper
	 * 
	 * EditorLocatorSmokeTest
	 * NavigatorDoubleClickTest
	 * ProblemViewTreeItemLocatorTest
	 * SectionLocatorSmokeTest
	 * ToolAndViewPullDownMenuSmokeTest
	 * 
	 * BEAContextMenuTest
	 * CodeCoverageViewTest
	 * FormControlsTest
	 * ImportExampleProjectTest
	 * ImportProjectTest
	 * LegacyExceptionTest
	 * ModifyCompilerSettingsTest
	 * NewSimpleProjectTest
	 * ProjectExplorerStressTest
	 * RecorderLaunchConfigTest
	 * TestTreeItemViewScope
	 * TextKeyStrokeSelectionTest
	 * 
	 * WindowTesterEvalRegTest
	 * 
	 * AbstractTreeItemLocatorTest
	 * AllSpecialCaseTreeItemLocatorTests
	 * TreeItemLocator2Test
	 * TreeItemLocatorCheckboxViewerDiagnosticTest
	 * TreeItemLocatorEscapedDoubleSlashTest
	 * TreeItemLocatorEscapedSlashTest
	 * TreeItemLocatorPerforceTeamTagsTest
	 * TreeItemLocatorSlashTest
	 * TreeItemLocatorTest
	 * 
	 * CodeCoveragePrefPageTest
	 * WindowTesterPrefPageTest
	 * WTRuntimePreferenceSettingsSmokeTest
	 * 
	 * RecorderIntegrationSmokeTest
	 * 
	 * EventSequenceLabelProviderSmokeTest
	 * 
	 * NewAPIScreenCaptureTest
	 * 
	 * Case41847_2Test
	 * Case41847Test
	 * 
	 * testClickLabeledTextFieldInNewProjectWizard
	 * testClickSyntaxTabItemInAntPrefPage
	 * testCreateNewProjectAndDelete
	 * testCreateProjectAndOpenType
	 * testJavaProjectClickInPackageExplorer
	 * testSelectPreferenceTreeItem
	 * testSimpleJavaProjectCreation
	 * testTableDoubleClick
	 * testTreeItemClickInNamedAntRuntimeTree
	 * */
	
	//TODO: test dynamic trees
	//TODO: test pattern matching
	
	//TODO: test tree with colums
	//TODO: implement "column filters", find treeItem by specifying column contents
	//TODO: implement and test code to record the correct TreeItemLocator parameters
	

}