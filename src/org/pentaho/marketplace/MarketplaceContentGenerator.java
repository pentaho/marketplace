/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pentaho.marketplace;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.pentaho.platform.api.engine.ServiceException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;

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
    public void test(OutputStream out) throws IOException {

        out.write("{test:123}".getBytes(ENCODING));
        setResponseHeaders(MIME_JSON, null);

    }

    private void setResponseHeaders(final String mimeType, final String attachmentName) {
        // Make sure we have the correct mime type
        final HttpServletResponse response = (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
        if (response == null) {
            return;
        }

        response.setHeader("Content-Type", mimeType);

        if (attachmentName != null) {
            response.setHeader("content-disposition", "attachment; filename=" + attachmentName);
        }

        // We can't cache this request
        response.setHeader("Cache-Control", "max-age=0, no-store");
    }
}
