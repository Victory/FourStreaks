package org.dfhu.fourstreaks;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.widget.Toast;

import java.util.Collection;
import java.util.Set;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testShouldPass () {
        assertTrue(true);
    }
}
