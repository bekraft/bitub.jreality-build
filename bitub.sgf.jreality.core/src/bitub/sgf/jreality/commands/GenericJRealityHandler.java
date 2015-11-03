/*
 * Copyright (c) 2012-2015 Bernold Kraft (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Initial commit by bernold @ 09.01.2014.
 */
package bitub.sgf.jreality.commands;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import bitub.sgf.jreality.views.JRealityViewPart;

/**
 * 
 * <!-- begin-user-doc -->
 * A generic jReality command handler. Determines the proper view part.
 * <!-- end-user-doc -->
 * @generated NOT
 * @author bernold - 09.01.2014
 */
public abstract class GenericJRealityHandler extends AbstractHandler
{
  final static Logger myLog = Logger.getLogger(GenericJRealityHandler.class);

  protected JRealityViewPart getView(ExecutionEvent event)
  {
    String activePartId = HandlerUtil.getActivePartId(event);
    return (JRealityViewPart)getView(event, activePartId);
  }

  protected JRealityViewPart getView(ExecutionEvent event, String viewID)
  {
    IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
    IViewPart viewPart = activePage.findView(viewID);
    if(null==viewPart) {
    
      try {
        
        viewPart = activePage.showView(viewID);
      }
      catch (PartInitException e) {
        
        myLog.error(String.format("View part \"%s\" not found.", viewID));        
      }
    }
    return (viewPart instanceof JRealityViewPart? (JRealityViewPart)viewPart: null);    
  }
    
}
