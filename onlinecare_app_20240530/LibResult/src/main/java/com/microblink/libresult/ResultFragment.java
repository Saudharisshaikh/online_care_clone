package com.microblink.libresult;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.microblink.libresult.extract.BaseRecognitionResultExtractor;
import com.microblink.libresult.extract.IBaseRecognitionResultExtractor;
import com.microblink.libresult.extract.RecognitionResultEntry;
import com.microblink.libresult.extract.austria.AustrianIDBackSideRecognitionResultExtractor;
import com.microblink.libresult.extract.austria.AustrianIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.austria.AustrianIDFrontSideRecognitionResultExtractor;
import com.microblink.libresult.extract.barcode.AztecRecognitionResultExtractor;
import com.microblink.libresult.extract.barcode.BardecoderRecognitionResultExtractor;
import com.microblink.libresult.extract.barcode.Pdf417RecognitionResultExtractor;
import com.microblink.libresult.extract.barcode.ZXingRecognitionResultExtractor;
import com.microblink.libresult.extract.blinkInput.BlinkOcrRecognitionResultExtractor;
import com.microblink.libresult.extract.croatia.CroatianIDBackSideRecognitionResultExtractor;
import com.microblink.libresult.extract.croatia.CroatianIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.croatia.CroatianIDFrontSideRecognitionResultExtractor;
import com.microblink.libresult.extract.czechia.CzechIDBackSideRecognitionResultExtractor;
import com.microblink.libresult.extract.czechia.CzechIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.czechia.CzechIDFrontSideRecognitionResultExtractor;
import com.microblink.libresult.extract.eudl.EUDLRecognitionResultExtractor;
import com.microblink.libresult.extract.germany.GermanIDBackSideRecognitionResultExtractor;
import com.microblink.libresult.extract.germany.GermanIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.germany.GermanIDFrontSideRecognitionResultExtractor;
import com.microblink.libresult.extract.germany.GermanOldIDRecognitionResultExtractor;
import com.microblink.libresult.extract.germany.GermanPassportRecognitionResultExtractor;
import com.microblink.libresult.extract.malaysia.IKadRecognitionResultExtractor;
import com.microblink.libresult.extract.malaysia.MyKadRecognitionResultExtractor;
import com.microblink.libresult.extract.mrtd.MRTDRecognitionResultExtractor;
import com.microblink.libresult.extract.romania.RomanianIDFrontSideRecognitionResultExtractor;
import com.microblink.libresult.extract.serbia.SerbianIDBackRecognitionResultExtractor;
import com.microblink.libresult.extract.serbia.SerbianIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.serbia.SerbianIDFrontRecognitionResultExtractor;
import com.microblink.libresult.extract.simnumber.SimNumberRecognitionResultExtractor;
import com.microblink.libresult.extract.singapore.SingaporeIDBackRecognitionResultExtractor;
import com.microblink.libresult.extract.singapore.SingaporeIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.singapore.SingaporeIDFrontRecognitionResultExtractor;
import com.microblink.libresult.extract.slovakia.SlovakIDBackSideRecognitionResultExtractor;
import com.microblink.libresult.extract.slovakia.SlovakIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.slovakia.SlovakIDFrontSideRecognitionResultExtractor;
import com.microblink.libresult.extract.slovenia.SlovenianIDBackRecognitionResultExtractor;
import com.microblink.libresult.extract.slovenia.SlovenianIDCombinedRecognitionResultExtractor;
import com.microblink.libresult.extract.slovenia.SlovenianIDFrontRecognitionResultExtractor;
import com.microblink.locale.LanguageUtils;
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.blinkbarcode.aztec.AztecScanResult;
import com.microblink.recognizers.blinkbarcode.bardecoder.BarDecoderScanResult;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417ScanResult;
import com.microblink.recognizers.blinkbarcode.simnumber.SimNumberScanResult;
import com.microblink.recognizers.blinkbarcode.zxing.ZXingScanResult;
import com.microblink.recognizers.blinkid.austria.back.AustrianIDBackSideRecognitionResult;
import com.microblink.recognizers.blinkid.austria.combined.AustrianIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.austria.front.AustrianIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkid.croatia.back.CroatianIDBackSideRecognitionResult;
import com.microblink.recognizers.blinkid.croatia.combined.CroatianIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.croatia.front.CroatianIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkid.czechia.back.CzechIDBackSideRecognitionResult;
import com.microblink.recognizers.blinkid.czechia.combined.CzechIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.czechia.front.CzechIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkid.eudl.EUDLRecognitionResult;
import com.microblink.recognizers.blinkid.germany.back.GermanIDBackSideRecognitionResult;
import com.microblink.recognizers.blinkid.germany.combined.GermanIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.germany.front.GermanIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkid.germany.old.front.GermanOldIDRecognitionResult;
import com.microblink.recognizers.blinkid.germany.passport.GermanPassportRecognitionResult;
import com.microblink.recognizers.blinkid.malaysia.IKadRecognitionResult;
import com.microblink.recognizers.blinkid.malaysia.MyKadRecognitionResult;
import com.microblink.recognizers.blinkid.mrtd.MRTDRecognitionResult;
import com.microblink.recognizers.blinkid.romania.front.RomanianIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkid.serbia.back.SerbianIDBackSideRecognitionResult;
import com.microblink.recognizers.blinkid.serbia.combined.SerbianIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.serbia.front.SerbianIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkid.singapore.back.SingaporeIDBackRecognitionResult;
import com.microblink.recognizers.blinkid.singapore.combined.SingaporeIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.singapore.front.SingaporeIDFrontRecognitionResult;
import com.microblink.recognizers.blinkid.slovakia.back.SlovakIDBackSideRecognitionResult;
import com.microblink.recognizers.blinkid.slovakia.combined.SlovakIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.slovakia.front.SlovakIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkid.slovenia.back.SlovenianIDBackSideRecognitionResult;
import com.microblink.recognizers.blinkid.slovenia.combined.SlovenianIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.slovenia.front.SlovenianIDFrontSideRecognitionResult;
import com.microblink.recognizers.blinkocr.BlinkOCRRecognitionResult;

import java.util.List;

/**
 * Created by igor on 12/2/14.
 */
public class ResultFragment extends Fragment {

    private static final String DATA = "DATA";
    public static final String RESULT_PARCELABLE = "RESULT_PARCELABLE";

    IBaseRecognitionResultExtractor mResultExtractor = null;
    public static List<RecognitionResultEntry> extractedData;
    public static String customerFullName = "",customerId = "";
    private BaseRecognitionResult mData = null;

    private AbsListView mListView;
    private ListAdapter mAdapter;

    // newInstance constructor for creating fragment with arguments
    public static ResultFragment newInstance(Parcelable result) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(RESULT_PARCELABLE, result);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        LanguageUtils.setLanguageConfiguration(getResources());
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(DATA)) {
            mData = savedInstanceState.getParcelable(DATA);
        }
        if (mData == null) {
            Bundle extras = getArguments();
            if (extras != null) {
                mData =  extras.getParcelable(RESULT_PARCELABLE);
            }
        }

        // CroatianIDBackSideRecognitionResult extends MRTDRecognitionResult so we first need
        // to check for CroatianIDBackSideRecognitionResult and then for MRTDRecognitionResult

        if (mData instanceof SingaporeIDFrontRecognitionResult) {
            mResultExtractor = new SingaporeIDFrontRecognitionResultExtractor(getActivity());
        } else if ( mData instanceof SingaporeIDBackRecognitionResult) {
            mResultExtractor = new SingaporeIDBackRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SingaporeIDCombinedRecognitionResult) {
            mResultExtractor = new SingaporeIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof AustrianIDBackSideRecognitionResult) {
            mResultExtractor = new AustrianIDBackSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof AustrianIDFrontSideRecognitionResult) {
            mResultExtractor = new AustrianIDFrontSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof AustrianIDCombinedRecognitionResult) {
            mResultExtractor = new AustrianIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof CroatianIDBackSideRecognitionResult) {
            mResultExtractor = new CroatianIDBackSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof CroatianIDFrontSideRecognitionResult) {
            mResultExtractor = new CroatianIDFrontSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof CroatianIDCombinedRecognitionResult) {
            mResultExtractor = new CroatianIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof CzechIDBackSideRecognitionResult) {
            mResultExtractor = new CzechIDBackSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof CzechIDFrontSideRecognitionResult) {
            mResultExtractor = new CzechIDFrontSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof CzechIDCombinedRecognitionResult) {
            mResultExtractor = new CzechIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof GermanIDBackSideRecognitionResult) {
            mResultExtractor = new GermanIDBackSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof GermanIDFrontSideRecognitionResult) {
            mResultExtractor = new GermanIDFrontSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof GermanOldIDRecognitionResult) {
            mResultExtractor = new GermanOldIDRecognitionResultExtractor(getActivity());
        } else if (mData instanceof GermanPassportRecognitionResult) {
            mResultExtractor = new GermanPassportRecognitionResultExtractor(getActivity());
        } else if (mData instanceof GermanIDCombinedRecognitionResult) {
            mResultExtractor = new GermanIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof RomanianIDFrontSideRecognitionResult) {
            mResultExtractor = new RomanianIDFrontSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SlovakIDBackSideRecognitionResult) {
            mResultExtractor = new SlovakIDBackSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SlovakIDFrontSideRecognitionResult) {
            mResultExtractor = new SlovakIDFrontSideRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SlovakIDCombinedRecognitionResult) {
            mResultExtractor = new SlovakIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SerbianIDBackSideRecognitionResult) {
            mResultExtractor = new SerbianIDBackRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SerbianIDFrontSideRecognitionResult) {
            mResultExtractor = new SerbianIDFrontRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SerbianIDCombinedRecognitionResult) {
            mResultExtractor = new SerbianIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SlovenianIDBackSideRecognitionResult) {
            mResultExtractor = new SlovenianIDBackRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SlovenianIDFrontSideRecognitionResult) {
            mResultExtractor = new SlovenianIDFrontRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SlovenianIDCombinedRecognitionResult) {
            mResultExtractor = new SlovenianIDCombinedRecognitionResultExtractor(getActivity());
        } else if (mData instanceof IKadRecognitionResult) {
            mResultExtractor = new IKadRecognitionResultExtractor(getActivity());
        } else if(mData instanceof MRTDRecognitionResult) {
            mResultExtractor = new MRTDRecognitionResultExtractor(getActivity());
        } else if(mData instanceof EUDLRecognitionResult) {
            mResultExtractor = new EUDLRecognitionResultExtractor(getActivity());
        } else if (mData instanceof Pdf417ScanResult) {
            mResultExtractor = new Pdf417RecognitionResultExtractor(getActivity());
        } else if (mData instanceof ZXingScanResult) {
            mResultExtractor = new ZXingRecognitionResultExtractor(getActivity());
        } else if (mData instanceof BarDecoderScanResult) {
            mResultExtractor = new BardecoderRecognitionResultExtractor(getActivity());
        } else if (mData instanceof SimNumberScanResult) {
            mResultExtractor = new SimNumberRecognitionResultExtractor(getActivity());
        } else if (mData instanceof AztecScanResult) {
            mResultExtractor = new AztecRecognitionResultExtractor(getActivity());
        } else if (mData instanceof BlinkOCRRecognitionResult) {
            mResultExtractor = new BlinkOcrRecognitionResultExtractor(getActivity());
        } else if (mData instanceof MyKadRecognitionResult) {
            mResultExtractor = new MyKadRecognitionResultExtractor(getActivity());
        } else {
            mResultExtractor = new BaseRecognitionResultExtractor(getActivity());
        }

        // Extract data from BaseRecognitionResult
        extractedData = mResultExtractor.extractData(mData);
        if (extractedData.size() < 1) {
            Toast.makeText(getActivity(), "Result list is empty", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }


        for (RecognitionResultEntry entry: extractedData) {
            System.out.println("-- Key: "+entry.getKey()+" Value: "+entry.getValue());
            if(entry.getKey().equalsIgnoreCase("Secondary ID")){//Customer Name
                customerFullName = entry.getValue();
            }
            if(entry.getKey().equalsIgnoreCase("Document number")){//Customer ID Number
                customerId = entry.getValue();
            }
        }


        getActivity().finish();
        // Create adapter which will be used to populate ListView.
        mAdapter = new ResultEntryAdapter(getActivity(), R.layout.result_entry, extractedData);

        // Hide virtual keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listView = new ListView(getActivity());

        listView.setAdapter(mAdapter);
        // some id is required so that android can save listview's scroll state
        // when activity goes to background
        // the id does not need to be unique
        listView.setId(android.R.id.text2);
        return listView;
    }

    @Override
    public void onResume() {
        LanguageUtils.setLanguageConfiguration(getResources());
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mData != null) {
            outState.putParcelable(DATA, mData);
        }
        super.onSaveInstanceState(outState);
    }
}
