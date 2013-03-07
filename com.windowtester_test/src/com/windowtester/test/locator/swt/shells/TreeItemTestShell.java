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
package com.windowtester.test.locator.swt.shells;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeItemTestShell {

	public Tree tree;
	private Shell shell;

	public Shell getShell() {
		return shell;
	}

	/**
	 * Open the window
	 */
	public void open() {
		shell = new Shell();
		createContents();
		shell.open();

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData());
		composite.setLayout(new GridLayout());

		tree = new Tree(composite, SWT.BORDER | SWT.MULTI);
		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL, GridData.FILL_VERTICAL, true, true);
		gridData.widthHint = 330;
		gridData.heightHint = 350;
		tree.setLayoutData(gridData);

		/*
		 * Tree items
		 */
		TreeItem rootItem = new TreeItem(tree, 0);
		rootItem.setText("Root");
			
			TreeItem label1_1 = new TreeItem(rootItem, 0);
			label1_1.setText("label1");
				new TreeItem(label1_1, 0).setText("label4");
			
			new TreeItem(rootItem, 0).setText("label2");
			
			TreeItem label1_2 = new TreeItem(rootItem, 0);
			label1_2.setText("label1");
				TreeItem label4_2 = new TreeItem(label1_2, 0);
				label4_2.setText("label4");
					new TreeItem(label4_2, 0).setText("label5");
			
			TreeItem label3_1 = new TreeItem(rootItem, 0);
			label3_1.setText("label3");
				new TreeItem(label3_1, 0).setText("label6");
			
			TreeItem label1_3 = new TreeItem(rootItem, 0);
			label1_3.setText("label1");
				new TreeItem(label1_3, 0).setText("label4");

		shell.layout();
	}

	void createContents() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);
		shell.setSize(380, 420);
		shell.setText("Tree Test");
	}

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TreeItemTestShell window = new TreeItemTestShell();
			window.open();

			// new EventRecordingWatcher(window.getShell()).watch();

			final Display display = Display.getDefault();
			while (!window.getShell().isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}