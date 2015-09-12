package de.pajowu.donate;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.appspot.donate_backend.donate.*;
import com.appspot.donate_backend.donate.Donate.Builder;
import com.appspot.donate_backend.donate.model.*;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

import com.github.androidprogresslayout.ProgressLayout;

import org.json.JSONObject;
import java.util.Map;
import java.util.List;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Iterator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.melnykov.fab.FloatingActionButton;
public class ProductFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    private final String TAG = "MainActivity";
    private View viewRoot;
    //TODO Get Labels, Sub_labels, categories, images, objects
    Context mContext;
    Long primaryKey;
    OfferProtoIdTitleSubtitleDescriptionCategoriesImageUrlsLatLonOwnerEndDateOwnerKey offer_data;
    Map<String,Object> im;
    String gplus_url = "";
    ArrayList<ContactRow> im_data;
    public ProductFragment(Context context, Long pk) {
        this.mContext = context;
        this.primaryKey = pk;
        Log.d("MainActivity",new Long(pk).toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewRoot = inflater.inflate(R.layout.fragment_product, container, false);

        //Implementation of custom Toolbar
        SpannableString s = new SpannableString(getString(R.string.app_name));
        s.setSpan(new de.pajowu.donate.TypefaceSpan(mContext, "fabiolo.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((MaterialNavigationDrawer) this.getActivity()).getToolbar().setTitle(s);

        loadFragmentData();

        return viewRoot;

    }
    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
    @Override
    public void onClick(View v) {
        Log.d("MainActivity gp1",gplus_url);
        Log.d("MainActivity gp2",((MainActivity)getActivity()).gplus_url);
        if (gplus_url.equals(((MainActivity)getActivity()).gplus_url)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Are you sure?");
            alert.setMessage("Are you sure that you want to delete your offer?");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ((ProgressLayout) viewRoot.findViewById(R.id.progress_layout)).showProgress();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Builder endpointBuilder = new Donate.Builder(
                                    AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(),
                                    CloudEndpointBuilderHelper.getRequestInitializer());

                            Donate service = CloudEndpointBuilderHelper.updateBuilder(endpointBuilder).build();
                            try {
                                service.offer().delete(new OfferProtoId().setId(primaryKey)).execute();
                            } catch (UserRecoverableAuthIOException e) {
                                final UserRecoverableAuthIOException e2 = e;
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        startActivityForResult(e2.getIntent(), 2);
                                    }
                                });
                                Log.d("MainActivity", "e", e);
                            } catch (Exception e) {
                                Log.d("MainActivity", "e", e);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    ((ProgressLayout) viewRoot.findViewById(R.id.progress_layout)).showContent();
                                    LocalFragment newFragment = new LocalFragment();
                                    ((MaterialNavigationDrawer) getActivity()).setFragment(newFragment,"Local");
                                }
                            });

                        }
                    };
                new Thread(runnable).start();
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            alert.show();
        } else {
            ContactFragment contactFragment = new ContactFragment(mContext, im_data);
            ((MaterialNavigationDrawer) getActivity()).setFragment(contactFragment,"Owner");
        }
        
    }
    private void fillLayout() {
        Log.d("MainActivity",((MainActivity)getActivity()).gplus_url);
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.fragment_product_mainImage);
        getActivity().findViewById(R.id.fragment_product_messageButton).setOnClickListener(this);
        gplus_url = (String)((HashMap)im.get("gplus")).get("url");
        if (gplus_url.equals(((MainActivity)getActivity()).gplus_url)) {
            ((FloatingActionButton) getActivity().findViewById(R.id.fragment_product_messageButton)).setImageResource(R.drawable.ic_delete);
        }
        im_data = new ArrayList<ContactRow>();
        for (Map.Entry<String, Object> entry : im.entrySet()) {
            if (entry.getValue().getClass().equals(String.class)) {
                im_data.add(new ContactRow(mContext, (String)entry.getValue(), entry.getKey()));
            }
            if (entry.getValue().getClass().equals(HashMap.class)) {
                try {
                    im_data.add(new ContactRow(mContext, (String)((HashMap)entry.getValue()).get("display"), entry.getKey(),  (String)((HashMap)entry.getValue()).get("url")));

                } catch (Exception e) {
                    Log.d(TAG,"Error",e);
                }
            }
            Log.d("MainActivity",entry.getValue().getClass().toString());
        }
        //roundedImageView = (RoundedImageView) getActivity().findViewById(R.id.fragment_product_productImage);

        // Log.d("primaryKey would be: ", "" + primaryKey);


        // Setting Main Title Typeface and appearance
        /*mainTitle = (TextView) getActivity().findViewById(R.id.app_bar_title);
        mainTitleTypeface = Typeface.createFromAsset(getAssets(), "fabiolo.otf");
        mainTitle.setTypeface(mainTitleTypeface);*/

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());
        float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());

        float ht_px2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
        if (offer_data.getImageUrls() != null) {
            Picasso.with(getActivity().getApplication()).load(offer_data.getImageUrls().get(0) + "=s" + metrics.widthPixels).resize(metrics.widthPixels, (int) ht_px2).into(imageView);
        }
        //TextView offerTextView = (TextView) getActivity().findViewById(R.id.offer);
        TextView titleTextView = (TextView) getActivity().findViewById(R.id.title);
        TextView subtitleTextView = (TextView) getActivity().findViewById(R.id.subtitle);
        TextView offerTextView = (TextView) getActivity().findViewById(R.id.offerText);

        titleTextView.setText(offer_data.getTitle());
        subtitleTextView.setText(offer_data.getSubtitle());
        offerTextView.setText(offer_data.getDescription());
    }

    public void loadFragmentData() {

        ((ProgressLayout) viewRoot.findViewById(R.id.progress_layout)).showProgress();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Builder endpointBuilder = new Donate.Builder(
                        AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(),
                        CloudEndpointBuilderHelper.getRequestInitializer());

                Donate service = CloudEndpointBuilderHelper.updateBuilder(endpointBuilder).build();

                
                try {
                    offer_data = service.offer().get(primaryKey).execute();
                    Log.d("MainActivity",offer_data.getOwner().toString());
                    im =  jsonToMap(new JSONObject(offer_data.getOwner().getIm().toString()));

                } catch (UserRecoverableAuthIOException e) {
                    final UserRecoverableAuthIOException e2 = e;
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            startActivityForResult(e2.getIntent(), 2);
                        }
                    });
                    Log.d("MainActivity", "e", e);
                } catch (Exception e) {
                    Log.d("MainActivity", "e", e);
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ((ProgressLayout) viewRoot.findViewById(R.id.progress_layout)).showContent();
                        fillLayout();
                    }
                });

            }
        };
        new Thread(runnable).start();
    }

}

