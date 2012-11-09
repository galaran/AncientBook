package mccity.plugins.ancientbook.bookworm;

import me.galaran.bukkitutils.ancientbook.UnicodeBOMInputStream;
import me.galaran.bukkitutils.ancientbook.WordWrapper;
import org.apache.commons.lang.Validate;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class WormBook {

    private final short id;
    private final String title;
    private final String author;
    private final String[] pages;

    private static final int MAX_CHARS_ON_LINE = 16;
    private static final int MAX_LINES_ON_PAGE = 13;

    public WormBook(File file) throws IOException {
        UnicodeBOMInputStream bomStream = new UnicodeBOMInputStream(new FileInputStream(file));
        BufferedReader bw = new BufferedReader(new InputStreamReader(bomStream, "utf-8"));
        bomStream.skipBOM();

        id = Short.parseShort(bw.readLine());
        title = bw.readLine();
        author = bw.readLine();
        String text = bw.readLine();
        bw.close();

        Validate.notNull(title, "Title is null in the wormbook " + id);
        Validate.notNull(author, "Author is null in the wormbook " + id);
        Validate.notNull(text, "Content is null in the wormbook " + id);

        pages = textToPages(text);
    }

    private static String[] textToPages(String rawText) {
        List<String> pages = new ArrayList<String>();

        String linedText = rawText.replaceAll("::", "\n");
        linedText = filterText(linedText);

        StringBuilder pageBuffer = new StringBuilder();
        int linesOnPage = 0;
        for (String curLine : WordWrapper.wrap(linedText, MAX_CHARS_ON_LINE, MAX_CHARS_ON_LINE / 3)) {
            pageBuffer.append(curLine);
            pageBuffer.append('\n');
            linesOnPage++;

            if (linesOnPage == MAX_LINES_ON_PAGE) {
                // next page
                pages.add(pageBuffer.toString());
                pageBuffer.setLength(0);
                linesOnPage = 0;
            }
        }
        if (pageBuffer.length() != 0) {
            pages.add(pageBuffer.toString());
        }

        return pages.toArray(new String[pages.size()]);
    }

    private static String filterText(String text) {
        char[] replaceManyInRowChars = new char[] { '~', '=', '-', ':', ';', '+', '!', '@', '#', '$', '%', '*', '_' };
        for (char replaceManyInRowChar : replaceManyInRowChars) {
            String pattern = Pattern.quote(String.valueOf(replaceManyInRowChar));

            char[] replacing = new char[MAX_CHARS_ON_LINE];
            Arrays.fill(replacing, replaceManyInRowChar);
            String replacingString = new String(replacing);

            text = text.replaceAll(pattern + "{" + MAX_CHARS_ON_LINE + ",}", replacingString);
        }
        return text;
    }

    public short getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String[] getPages() {
        return pages;
    }
}
