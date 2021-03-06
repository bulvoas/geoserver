package org.geoserver.gwc.web;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.web.GeoServerHomePage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Test;

public class GWCServiceLinksTest extends GeoServerWicketTestSupport {

    
    @Test
    public void testCapabilitiesLinks() {
        GeoServerHomePage page = new GeoServerHomePage();

        tester.startPage(page);
        tester.assertRenderedPage(GeoServerHomePage.class);

        Page lastPage = tester.getLastRenderedPage();
        final List<String> services = new ArrayList<String>();
        lastPage.visitChildren(ExternalLink.class, new IVisitor<ExternalLink>() {

            @Override
            public Object component(ExternalLink component) {
                String url = (String) component.getDefaultModelObject();
                if(url != null) {
                    if(url.startsWith("../gwc/service/")) {
                        int idx = url.indexOf("?");
                        String service;
                        if(idx > 0) {
                            service = url.substring("./gwc/service/".length() + 1, idx);
                        } else {
                            service = url.substring("./gwc/service/".length() + 1);
                        }
                        if(service != null) {
                            services.add(service);
                        }
                    } else if(url.contains("GetCapabilities")){
                        Map<String, Object> params = KvpUtils.parseQueryString(url);
                        String service = (String) params.get("service");
                        if(service != null) {
                            services.add(service);
                        }
                    }
                }
                
                return IVisitor.CONTINUE_TRAVERSAL;
            }
            
        });
        
        // GEOS-5886
        assertFalse(services.contains("gwc"));
        // these come from the custom provider
        assertTrue(services.contains("wmts"));
        assertTrue(services.contains("wms"));
        assertTrue(services.contains("tms/1.0.0"));
    }

}
