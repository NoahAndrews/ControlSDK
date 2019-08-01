package org.btelman.controlsdk.hardware.interfaces

/**
 * Annotation for Translators so we have a way to find them all
 *
 * Anything with this annotation would show up in potential displays of components if the implementing app supports it.
 *
 * Keep in mind that if the activity name or package changes,
 * it may break settings since the class it references no longer exists
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TranslatorComponent