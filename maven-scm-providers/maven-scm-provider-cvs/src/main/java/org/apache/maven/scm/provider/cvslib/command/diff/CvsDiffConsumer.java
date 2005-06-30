package org.apache.maven.scm.provider.cvslib.command.diff;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.log.ScmLogger;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 * @todo share with SVN (3 extra lines can be ignored)
 */
public class CvsDiffConsumer
    implements StreamConsumer
{
//
// Index: plugin.jelly
// ===================================================================
// RCS file: /home/cvs/maven-scm/maven-scm-providers/maven-scm-provider-cvs/src/main/resources/META-INF/plexus/components.xml,v
// retrieving revision 1.2
// diff -u -r1.2 components.xml
// --- plugin.jelly        (revision 124799)
// +++ plugin.jelly        (working copy)
//

    private final static String RCS_TOKEN = "RCS file: ";

    private final static String RETRIEVING_TOKEN = "retrieving revision ";

    private final static String DIFF_TOKEN = "diff ";

    private final static String INDEX_TOKEN = "Index: ";

    private final static String FILE_SEPARATOR_TOKEN = "===";

    private final static String START_REVISION_TOKEN = "---";

    private final static String END_REVISION_TOKEN = "+++";

    private final static String ADDED_LINE_TOKEN = "+";

    private final static String REMOVED_LINE_TOKEN = "-";

    private final static String UNCHANGED_LINE_TOKEN = " ";

    private final static String CHANGE_SEPARATOR_TOKEN = "@@";

    private final static String NO_NEWLINE_TOKEN = "\\ No newline at end of file";

    private ScmLogger logger;

    private File workingDirectory;

    private String currentFile;

    private StringBuffer currentDifference;

    private List changedFiles = new ArrayList();

    private Map differences = new HashMap();

    private StringBuffer patch = new StringBuffer();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public CvsDiffConsumer( ScmLogger logger, File workingDirectory )
    {
        this.logger = logger;

        this.workingDirectory = workingDirectory;
    }

    // ----------------------------------------------------------------------
    // StreamConsumer Implementation
    // ----------------------------------------------------------------------

    public void consumeLine( String line )
    {
        if ( line.startsWith( INDEX_TOKEN ) )
        {
            // start a new file
            currentFile = line.substring( INDEX_TOKEN.length() );

            changedFiles.add( new ScmFile( currentFile, ScmFileStatus.MODIFIED ) );

            currentDifference = new StringBuffer();

            differences.put( currentFile, currentDifference );

            patch.append( line ).append( "\n" );

            return;
        }

        if ( currentFile == null )
        {
            logger.warn( "Unparseable line: '" + line + "'" );
            patch.append( line ).append( "\n" );
            return;
        }

        if ( line.startsWith( FILE_SEPARATOR_TOKEN ) )
        {
            // skip
            patch.append( line ).append( "\n" );
        }
        else if ( line.startsWith( START_REVISION_TOKEN ) )
        {
            // skip, though could parse to verify filename, start revision
            patch.append( line ).append( "\n" );
        }
        else if ( line.startsWith( END_REVISION_TOKEN ) )
        {
            // skip, though could parse to verify filename, end revision
            patch.append( line ).append( "\n" );
        }
        else if ( line.startsWith( RCS_TOKEN ) )
        {
            // skip, though could parse to verify filename
        }
        else if ( line.startsWith( RETRIEVING_TOKEN ) )
        {
            // skip, though could parse to verify version
        }
        else if ( line.startsWith( DIFF_TOKEN ) )
        {
            // skip, though could parse to verify command
        }
        else if ( line.startsWith( ADDED_LINE_TOKEN ) || line.startsWith( REMOVED_LINE_TOKEN ) ||
            line.startsWith( UNCHANGED_LINE_TOKEN ) || line.startsWith( CHANGE_SEPARATOR_TOKEN ) ||
            line.equals( NO_NEWLINE_TOKEN ) )
        {
            // add to buffer
            currentDifference.append( line ).append( "\n" );
            patch.append( line ).append( "\n" );
        }
        else
        {
            logger.warn( "Unparseable line: '" + line + "'" );
            patch.append( line ).append( "\n" );
            // skip to next file
            currentFile = null;
            currentDifference = null;
        }
    }

    public List getChangedFiles()
    {
        return changedFiles;
    }

    public Map getDifferences()
    {
        return differences;
    }

    public String getPatch()
    {
        return patch.toString();
    }

}