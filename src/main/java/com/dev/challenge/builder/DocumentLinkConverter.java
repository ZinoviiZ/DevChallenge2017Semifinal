package com.dev.challenge.builder;

/**
 * Links of site. Util links processing;
 */
public class DocumentLinkConverter {

    public static final String ROOT_URL = "http://brovary-rada.gov.ua";
    public static final String PAGINATOR_URL = "http://brovary-rada.gov.ua/documents/?start=";
    public static final String DOCUMENT_URL = "http://brovary-rada.gov.ua/documents/{DOCUMENT_ID}.html";

    /**
     * Parse document id from url
     * @param documentUrl
     * @return Document id
     */
    public static String getDocumentId(String documentUrl) {

        int fromIndex = documentUrl.indexOf("/documents/") + 11;
        int toIndex = documentUrl.indexOf(".html");
        return documentUrl.substring(fromIndex, toIndex);
    }

    /**
     * Build url by document id
     * @param id
     * @return Document url
     */
    public static String buildDocumentLink(String id) {
        return DOCUMENT_URL.replace("{DOCUMENT_ID}", id);
    }
}
