/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pentaho.marketplace;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pentaho.platform.api.engine.ServiceException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.annotations.OutputType;

/**
 *
 * @author pedro
 */
public class MarketplaceContentGenerator extends SimpleContentGenerator {

    private static final long serialVersionUID = 1L;
    public static final String ENCODING = "utf-8";
    private static final String MIME_JSON = "application/json";

    protected MarketplaceService getMarketplaceService() throws ServiceException {


        return ((MarketplaceService) PentahoSystem.get(org.pentaho.platform.api.engine.IServiceManager.class).getServiceBean("xml", "MarketplaceService"));

    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    @OutputType(MIME_JSON)
    public void test(OutputStream out) throws IOException {
        try {
            out.write(getMarketplaceService().getPluginsJson().getBytes(ENCODING));
        } catch (ServiceException ex) {
            logger.error(ex);
        }
    }
}
