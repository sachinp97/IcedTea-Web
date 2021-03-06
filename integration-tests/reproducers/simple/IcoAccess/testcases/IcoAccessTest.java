/* SimpleTest1Test.java
 Copyright (C) 2011 Red Hat, Inc.

 This file is part of IcedTea.

IcedTea is free software; you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation, version 2.

IcedTea is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
IcedTea; see the file COPYING. If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is making a
combined work based on this library. Thus, the terms and conditions of the GNU
General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent modules, and
to copy and distribute the resulting executable under terms of your choice,
provided that you also meet, for each linked independent module, the terms and
conditions of the license of that module. An independent module is a module
which is not derived from or based on this library. If you modify this library,
you may extend this exception to your version of the library, but you are not
obligated to do so. If you do not wish to do so, delete this exception
statement from your version.
*/

import net.sourceforge.jnlp.OptionsDefinitions;
import net.sourceforge.jnlp.ProcessResult;
import net.sourceforge.jnlp.ServerAccess;
import net.sourceforge.jnlp.annotations.TestInBrowsers;
import net.sourceforge.jnlp.browsertesting.BrowserTest;
import net.sourceforge.jnlp.browsertesting.Browsers;
import net.sourceforge.jnlp.closinglisteners.AutoErrorClosingListener;
import net.sourceforge.jnlp.closinglisteners.AutoOkClosingListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class IcoAccessTest extends BrowserTest {

    @Test
    public void testJavawsCanUseIcoProvider() throws Exception {
        ProcessResult pr = server.executeJavaws(null, "IcoAccess.jnlp");
        Assert.assertTrue(pr.stdout.contains("IcoAccess running"));
        Assert.assertTrue(pr.stdout.matches("(?s).*http.*IcoAccess.ico.*"));
        Assert.assertTrue(pr.stdout.contains("array checks passed"));
        //javaws CAN access our registered provider
        Assert.assertTrue(pr.stdout.contains("ico: BufferedImage@"));
        Assert.assertFalse(pr.stdout.contains("ico: null"));
        Assert.assertTrue(pr.stdout.contains("IcoAccess ended"));
        Assert.assertTrue(pr.stdout.contains("png: BufferedImage@"));
        Assert.assertFalse(pr.stdout.contains("png: null"));

    }

    @Test
    //well this si reallys trange.
    //from outpouts its clear that ico provider IS registered..
    public void testJavawsHeadlessCanNotUseIcoProvider() throws Exception {
        ProcessResult pr = server.executeJavawsHeadless(null, "IcoAccess.jnlp");
        Assert.assertTrue(pr.stdout.contains("IcoAccess running"));
        Assert.assertTrue(pr.stdout.matches("(?s).*http.*IcoAccess.ico.*"));
        Assert.assertTrue(pr.stdout.contains("array checks passed"));
        //javaws CANNOT access our registered provider
        Assert.assertFalse(pr.stdout.contains("ico: BufferedImage@"));
        Assert.assertTrue(pr.stdout.contains("ico: null"));
        Assert.assertTrue(pr.stdout.contains("IcoAccess ended"));
        Assert.assertTrue(pr.stdout.contains("png: BufferedImage@"));
        Assert.assertFalse(pr.stdout.contains("png: null")); //ok, I dont understand this. Normal providers works...
    }

    @Test
    public void testJavawsHtmlCanUseIcoProvider() throws Exception {
        ProcessResult pr = server.executeJavaws(Arrays.asList(new String[]{OptionsDefinitions.OPTIONS.HTML.option}), "IcoAccess.html", new AutoOkClosingListener(), new AutoErrorClosingListener());
        Assert.assertTrue(pr.stdout.contains("IcoAccess running"));
        Assert.assertTrue(pr.stdout.matches("(?s).*http.*IcoAccess.ico.*"));
        Assert.assertTrue(pr.stdout.contains("array checks passed"));
        //javaws CAN access our registered provider
        Assert.assertTrue(pr.stdout.contains("ico: BufferedImage@"));
        Assert.assertFalse(pr.stdout.contains("ico: null"));
        Assert.assertTrue(pr.stdout.contains("IcoAccess ended"));
        Assert.assertTrue(pr.stdout.contains("png: BufferedImage@"));
    }

    @Test
    @TestInBrowsers(testIn = Browsers.one)
    public void testAppletCanUseIcoProvider() throws Exception {
        ProcessResult pr = server.executeBrowser("IcoAccess.html", ServerAccess.AutoClose.CLOSE_ON_BOTH);
        Assert.assertTrue(pr.stdout.contains("IcoAccess running"));
        Assert.assertTrue(pr.stdout.matches("(?s).*http.*IcoAccess.ico.*"));
        Assert.assertTrue(pr.stdout.contains("array checks passed"));
        //applets CAN access our registered provider
        Assert.assertTrue(pr.stdout.contains("ico: BufferedImage@"));
        Assert.assertFalse(pr.stdout.contains("ico: null"));
        Assert.assertTrue(pr.stdout.contains("IcoAccess ended"));
        Assert.assertTrue(pr.stdout.contains("png: BufferedImage@"));
        Assert.assertFalse(pr.stdout.contains("png: null"));
    }

}
