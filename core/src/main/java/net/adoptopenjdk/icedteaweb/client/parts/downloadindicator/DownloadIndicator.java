// Copyright (C) 2001-2003 Jon A. Maxwell (JAM)
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

package net.adoptopenjdk.icedteaweb.client.parts.downloadindicator;

import javax.jnlp.DownloadServiceListener;
import java.net.URL;

/**
 * A DownloadIndicator creates DownloadServiceListeners that are
 * notified of resources being transferred and their progress.
 *
 * @author <a href="mailto:jmaxwell@users.sourceforge.net">Jon A. Maxwell (JAM)</a> - initial author
 * @version $Revision: 1.8 $
 */
public interface DownloadIndicator {

    /**
     * Return a download service listener that displays the progress
     * of downloading resources. Update messages may be reported
     * for URLs that are not included initially.
     * <p>
     * Progress messages are sent as if the DownloadServiceListener
     * were listening to a DownloadService request. The listener
     * will receive progress messages from time to time during the
     * download.
     * </p>
     *
     * @param downloadName name identifying the download to the user
     * @param resources initial urls to display, empty if none known at start
     * @return dedicated listener
     */
    DownloadServiceListener getListener(String downloadName, URL[] resources);

    /**
     * Indicates that a download service listener that was obtained
     * from the getDownloadListener method will no longer be used.
     * This method can be used to ensure that progress dialogs are
     * properly removed once a particular download is finished.
     *
     * @param listener the listener that is no longer in use
     */
    void disposeListener(DownloadServiceListener listener);
}
