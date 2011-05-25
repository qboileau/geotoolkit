/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.DescribeProcess;

/**
 * Abstract get capabilities request.
 * 
 * @author Quentin Boileau
 * @module pending
 */
public abstract class AbstractDescribeProcess extends AbstractRequest implements DescribeProcessRequest{
    
    protected final String version;
    protected List<String> identifiers;
    
    
    protected AbstractDescribeProcess(final String serverURL,final String version){
        super(serverURL);
        this.version = version;
    }
    
    @Override
    public List<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public void setIdentifiers(List<String> identifiers) {
       this.identifiers = identifiers;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        requestParameters.put("SERVICE",    "WPS");
        requestParameters.put("REQUEST", "DescribeProcess");
        requestParameters.put("VERSION",    "1.0.0");
        
    }
    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {

        final DescribeProcess request = new DescribeProcess();
        request.setService("WPS");
        request.setVersion(version);
        final List<CodeType> listId = new ArrayList<CodeType>();
        for(String str : identifiers){
            listId.add(new CodeType(str));
        }
        request.getIdentifier().addAll(listId);

        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();
        Marshaller marshaller = null;
        try {
            marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
            marshaller.marshal(request, stream);
            //marshaller.marshal(request, System.out);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (marshaller != null) {
                WPSMarshallerPool.getInstance().release(marshaller);
            }
        }
        stream.close();
        return conec.getInputStream();
    }
}
