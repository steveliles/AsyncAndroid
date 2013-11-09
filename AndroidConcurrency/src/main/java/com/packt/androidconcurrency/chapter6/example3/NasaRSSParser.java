package com.packt.androidconcurrency.chapter6.example3;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NasaRSSParser {

    private SAXParserFactory factory;

    public NasaRSSParser(){
        factory = SAXParserFactory.newInstance();
    }

    public NasaRSS parse(InputStream in)
    throws Exception {
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        NasaRSSHandler rss = new NasaRSSHandler();
        reader.setContentHandler(rss);

        reader.parse(new InputSource(in));
        return rss.result;
    }

    class NasaRSSHandler extends DefaultHandler {
        NasaRSS result = new NasaRSS();

        private String el;
        private String url;
        private StringBuilder title = new StringBuilder();

        @Override
        public void startElement(
                String uri, String localName,
                String qName, Attributes attributes
        ) throws SAXException {
            if ("item".equals(qName)) {
                title.setLength(0);
                url = null;
            } else if ("enclosure".equals(qName)) {
                url = attributes.getValue("url");
            }
            el = localName;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if ("title".equals(el))
                title.append(ch,start,length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("item".equals(localName) && url != null) {
                result.add(url, title.toString());
            }
        }
    }

}
