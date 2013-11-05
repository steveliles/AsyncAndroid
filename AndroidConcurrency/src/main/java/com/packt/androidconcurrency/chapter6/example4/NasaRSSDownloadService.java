package com.packt.androidconcurrency.chapter6.example4;

import android.net.Uri;

import com.packt.androidconcurrency.chapter6.CacheDirCache;
import com.packt.androidconcurrency.chapter6.DownloadService;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NasaRSSDownloadService extends DownloadService<NasaRSS> {

    private SAXParserFactory factory;

    public NasaRSSDownloadService() {
        factory = SAXParserFactory.newInstance();
    }

    @Override
    protected Cache initCache()
    throws Exception {
        return new CacheDirCache(getApplicationContext());
    }

    @Override
    protected NasaRSS convert(Uri data)
    throws Exception {
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        NasaRSSHandler rss = new NasaRSSHandler();
        reader.setContentHandler(rss);

        InputStream in = getContentResolver().openInputStream(data);
        reader.parse(new InputSource(in));
        return rss.result;
    }

    class NasaRSSHandler extends DefaultHandler {
        NasaRSS result = new NasaRSS();

        @Override
        public void startElement(
            String uri, String localName,
            String qName, Attributes attributes
        ) throws SAXException {
            if ("enclosure".equals(qName))
                result.add(attributes.getValue("url"));
        }
    }
}
