package de.michiruf.android.licenses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import com.nhaarman.listviewanimations.util.AbsListViewWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Michael on 08.08.2015.
 *
 * @author Michael Ruf
 * @since 2015-08-08
 */
@SuppressLint("ViewConstructor")
public class LicenseView extends ListView {

    public static Options getDefaultOptions() {
        return new Options(true, "licenses");
    }

    private static String readInputStream(InputStream inputStream, String encoding) {
        Scanner s = new Scanner(inputStream, encoding).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @NonNull
    private final AssetManager assetManager;

    @NonNull
    private final Options options;

    public LicenseView(@NonNull Context context, @NonNull AssetManager assetManager) {
        this(context, assetManager, null);
    }

    public LicenseView(@NonNull Context context, @NonNull AssetManager assetManager,
                       @Nullable Options options) {
        super(context);
        this.assetManager = assetManager;
        if (options == null) {
            options = getDefaultOptions();
        }
        this.options = options;

        // Initialize the view stuff
        Adapter adapter = new Adapter();
        setAdapter(adapter);
        adapter.setListViewWrapper(new AbsListViewWrapper(this));

        // Initialize the data stuff
        loadAssets();
    }

    private void loadAssets() {
        if (!options.loadFromAssets) {
            return;
        }

        String path = options.assetLicenseDirectory;
        try {
            for (String file : assetManager.list(path)) {
                String license = readInputStream(assetManager.open(path + "/" + file), "UTF-8");
                getAdapter().add(new License(file, license));
            }
            getAdapter().notifyDataSetChanged();
        } catch (IOException e) {
            throw new IllegalArgumentException("Assets cannot get loaded for given options", e);
        }
    }

    @Override
    public Adapter getAdapter() {
        return (Adapter) super.getAdapter();
    }

    @SuppressWarnings("UnusedParameters")
    protected void customizeTitleView(TextView view, License item) {
    }

    @SuppressWarnings("UnusedParameters")
    protected void customizeContentView(TextView view, License item) {
    }

    public static final class License {

        private String headline;
        private String content;

        public License(String headline, String content) {
            this.headline = headline;
            this.content = content;
        }
    }

    public final class Adapter extends ExpandableListItemAdapter<License> {

        protected Adapter() {
            super(getContext());
        }

        @NonNull
        @Override
        public View getTitleView(int position, @Nullable View convertView, @NonNull ViewGroup viewGroup) {
            TextView view;
            if (convertView instanceof TextView) {
                view = (TextView) convertView;
            } else {
                view = new TextView(getContext());
            }

            License item = getItem(position);
            view.setText(item.headline);
            customizeTitleView(view, item);
            return view;
        }

        @NonNull
        @Override
        public View getContentView(int position, @Nullable View convertView, @NonNull ViewGroup viewGroup) {
            TextView view;
            if (convertView instanceof TextView) {
                view = (TextView) convertView;
            } else {
                view = new TextView(getContext());
            }

            License item = getItem(position);
            view.setText(item.content);
            customizeContentView(view, item);
            return view;
        }
    }

    public static class Options {

        private boolean loadFromAssets;

        private String assetLicenseDirectory;

        public Options(boolean loadFromAssets, String assetLicenseDirectory) {
            this.loadFromAssets = loadFromAssets;
            this.assetLicenseDirectory = assetLicenseDirectory;
        }
    }
}
