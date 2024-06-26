package ss.com.bannerslider.views.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ss.com.bannerslider.App;
import ss.com.bannerslider.R;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.AdjustableImageView;

/**
 * @author S.Shahini
 * @since 11/23/16
 */

public class BannerFragment extends Fragment {
    private Banner banner;

    public BannerFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        banner = getArguments().getParcelable("banner");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (banner != null) {

            final AdjustableImageView imageView = new AdjustableImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(banner.getScaleType());
            if (banner instanceof DrawableBanner) {
                DrawableBanner drawableBanner = (DrawableBanner) banner;
                //Picasso.with(getContext()).load(drawableBanner.getDrawable()).into(imageView);

                //App.loadImageFromDrawable(drawableBanner.getDrawable(),imageView, R.drawable.ic_placeholder_2,getActivity());
                imageView.setImageResource(drawableBanner.getDrawable());

            } else {
                final RemoteBanner remoteBanner = (RemoteBanner) banner;
                if (remoteBanner.getErrorDrawable() == null && remoteBanner.getPlaceHolder() == null) {
                    //Picasso.with(getActivity()).load(remoteBanner.getUrl()).into(imageView);
                    App.loadImageFromURL(remoteBanner.getUrl(),imageView, R.drawable.ic_placeholder_2,getActivity());
                } else {
                    if (remoteBanner.getPlaceHolder() != null && remoteBanner.getErrorDrawable() != null) {
                        //Picasso.with(getActivity()).load(remoteBanner.getUrl()).placeholder(remoteBanner.getPlaceHolder()).error(remoteBanner.getErrorDrawable()).into(imageView);
                        App.loadImageFromURL(remoteBanner.getUrl(),imageView, R.drawable.ic_placeholder_2,getActivity());
                    } else if (remoteBanner.getErrorDrawable() != null) {
                        //Picasso.with(getActivity()).load(remoteBanner.getUrl()).error(remoteBanner.getErrorDrawable()).into(imageView);
                        App.loadImageFromURL(remoteBanner.getUrl(),imageView, R.drawable.ic_placeholder_2,getActivity());
                    } else if (remoteBanner.getPlaceHolder() != null) {
                        //Picasso.with(getActivity()).load(remoteBanner.getUrl()).placeholder(remoteBanner.getPlaceHolder()).into(imageView);
                        App.loadImageFromURL(remoteBanner.getUrl(),imageView, R.drawable.ic_placeholder_2,getActivity());
                    }
                }
            }
            imageView.setOnTouchListener(banner.getOnTouchListener());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnBannerClickListener onBannerClickListener = banner.getOnBannerClickListener();
                    if (onBannerClickListener != null) {
                        onBannerClickListener.onClick(banner.getPosition());
                    }
                }
            });

            return imageView;
        } else {
            throw new RuntimeException("banner cannot be null");
        }
    }

    public static BannerFragment newInstance(Banner banner) {
        Bundle args = new Bundle();
        args.putParcelable("banner", banner);
        BannerFragment fragment = new BannerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
