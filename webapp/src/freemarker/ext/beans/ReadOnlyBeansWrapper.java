/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package freemarker.ext.beans;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** BeansWrapper that is more restrictive than EXPOSE_SAFE, by
 * exposing getters but not setters. A setter is defined for this
 * purpose as a method that returns void, or whose name
 * starts with "set". 
 * 
 * @author rjy7
 *
 */
public class ReadOnlyBeansWrapper extends BeansWrapper {

    private static final Log log = LogFactory.getLog(ReadOnlyBeansWrapper.class);
    
    public ReadOnlyBeansWrapper() {
        // Start by exposing all safe methods.
        setExposureLevel(EXPOSE_SAFE);
    }
    
    @Override
    protected void finetuneMethodAppearance(Class cls, Method method, MethodAppearanceDecision decision) {
        
        // How to define a setter? This is an approximation: a method whose name
        // starts with "set" or returns void.
        if ( method.getName().startsWith("set") ) {
            decision.setExposeMethodAs(null);
        } else if ( method.getReturnType().getName().equals("void") ) {
            decision.setExposeMethodAs(null);
        }        
    }
    
}
