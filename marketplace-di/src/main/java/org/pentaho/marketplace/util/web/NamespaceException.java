
/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.marketplace.util.web;

/**
 * A NamespaceException is thrown to indicate an error with the caller's request
 * to register a servlet or resources into the URI namespace of the Http
 * Service. This exception indicates that the requested alias already is in use.
 * 
 * @author $Id: d44893b09317a18af79f13d73b22c39d4f64549e $
 */
public class NamespaceException extends Exception {
	static final long	serialVersionUID	= 7235606031147877747L;

	/**
	 * Construct a {@code NamespaceException} object with a detail message.
	 * 
	 * @param message the detail message
	 */
	public NamespaceException(String message) {
		super(message);
	}

	/**
	 * Construct a {@code NamespaceException} object with a detail message and a
	 * nested exception.
	 * 
	 * @param message The detail message.
	 * @param cause The nested exception.
	 */
	public NamespaceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Returns the nested exception.
	 * 
	 * <p>
	 * This method predates the general purpose exception chaining mechanism.
	 * The {@code getCause()} method is now the preferred means of obtaining
	 * this information.
	 * 
	 * @return The result of calling {@code getCause()}.
	 */
	public Throwable getException() {
		return getCause();
	}

	/**
	 * Returns the cause of this exception or {@code null} if no cause was set.
	 * 
	 * @return The cause of this exception or {@code null} if no cause was set.
	 * @since 1.2
	 */
	@Override
	public Throwable getCause() {
		return super.getCause();
	}

	/**
	 * Initializes the cause of this exception to the specified value.
	 * 
	 * @param cause The cause of this exception.
	 * @return This exception.
	 * @throws IllegalArgumentException If the specified cause is this
	 *         exception.
	 * @throws IllegalStateException If the cause of this exception has already
	 *         been set.
	 * @since 1.2
	 */
	@Override
	public Throwable initCause(Throwable cause) {
		return super.initCause(cause);
	}
}
