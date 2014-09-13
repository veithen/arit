package com.googlecode.arit.resource;


/**
 * TODO: temporary until ResourceEnumerator one phased out. Then merge with ResourceScanner
 *
 * @param <T>
 */
public interface ResourceScannerFacet {

	boolean isAvailable();

	String getDescription();

}