package dk.dtu.lbs.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dk.dtu.lbs.activities.ProfileActivity;
import dk.dtu.lbs.database.TrackerDataSource;
import dk.dtu.lbs.dto.Profile;
import dk.dtu.lbs.interfaces.OnFragmentInteractionListener;
import dk.dtu.lbs.interfaces.TrackerRestService;
import dk.dtu.lbs.utils.AppUtil;
import dk.dtu.lbs.wsclient.RESTClient;
import dk.dtu.lbs.activities.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ShowProfile extends Fragment implements View.OnClickListener{
    private TextView name=null;
    private TextView phone=null;
    private TextView mail=null;
    private TextView description=null;
    private ImageView image=null;
    private Button editProfile=null;
    private Button deleteProfile=null;
    private Context context=null;
    private String TAG="ShowProfile";
    private OnFragmentInteractionListener mListener;
    private TrackerDataSource localDatabase=null;





    public static ShowProfile newInstance() {
        ShowProfile fragment = new ShowProfile();
        return fragment;
    }

    public ShowProfile() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localDatabase=TrackerDataSource.getInstance(this.getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_show_profile, container, false);
        name=(TextView)view.findViewById(R.id.show_nameTF);
        phone=(TextView)view.findViewById(R.id.show_phonTF);
        mail=(TextView)view.findViewById(R.id.show_mailTF);
        description=(TextView)view.findViewById(R.id.show_descriptionTF);
        image=(ImageView)view.findViewById(R.id.user_image);
        editProfile=(Button)view.findViewById(R.id.editProfileBT);
        editProfile.setOnClickListener(this);
        deleteProfile=(Button)view.findViewById(R.id.deleteProfileBT);
        deleteProfile.setOnClickListener(this);
        context=this.getActivity().getApplicationContext();
        showProfile();


        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener=null;

    }
    public void requestActivityForAction(String action) {
        if (mListener != null) {
            mListener.onFragmentInteraction(action);
        }
    }
    public void showProfile(){
        //List<Profile> profiles=LocalDatabase.getInstance().readProfile(Realm.getInstance(this.getActivity().getApplicationContext()));
        List<Profile> profiles=localDatabase.readProfile();

        Toast.makeText(this.getActivity().getApplicationContext(),"Size of profile list : "+profiles.size(),Toast.LENGTH_SHORT).show();
        if(!profiles.isEmpty()){
            Profile profile=profiles.get(0);
            name.setText(profile.getName());
            phone.setText(String.valueOf(profile.getPhone()));
            mail.setText(profile.getMail());
            description.setText(profile.getDescription());
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfileBT:
                requestActivityForAction(ProfileActivity.EDIT_PROFILE);
                break;
            case R.id.deleteProfileBT:
                delete();
                break;

        }

    }
    private void delete(){
        if(!AppUtil.isNetworkConnected(this.getActivity().getApplicationContext())){
            showNoNetworkDialog();
        }else{
            showDeleteDialog();
        }
    }
    private void showDeleteDialog(){

        String title="Confirm Delete";
        String msg="Are you sure you want to delete your profile";
        int icon=R.mipmap.ic_delete;

        final Dialog dialog=new Dialog(this.getActivity());
        View.OnClickListener yes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteProfile();
            }
        };
        View.OnClickListener no=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };

        AppUtil.prepareDialog(dialog,title,msg,icon,yes,no);
        dialog.show();

    }

    private void showNoNetworkDialog(){
        String title="No Network Connection";
        String msg="Turn on your network connection";
        int icon=R.mipmap.ic_no_network;
        final Dialog dialog=new Dialog(this.getActivity());
        AppUtil.prepareDialog(dialog, title, msg, icon, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }, new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    public void deleteProfile(){
        Log.d(TAG, "Delete Profile");
        //final Realm realm=Realm.getInstance(context);
        //List<Profile> profiles=LocalDatabase.getInstance().readProfile(realm);
        List<Profile> profiles=localDatabase.readProfile();
        Profile profile=null;
        if(!profiles.isEmpty()){
            profile=profiles.get(0);
        }
        if (profile !=null){
            Log.d(TAG,"User uid :"+profile.getUid());
        }
        TrackerRestService client= RESTClient.getClient(context);
        Call<Profile> call=client.deleteUser(profile.getUid());
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Response<Profile> response, Retrofit retrofit) {
                int statusCode = response.code();
                String msg = response.message();
                //status.setText("status code :" + statusCode + " message :" + msg);
                if (statusCode == 200) {
                    //LocalDatabase.getInstance().deleteProfile(realm);
                    localDatabase.deleteProfile();

                    //realm.close();
                    requestActivityForAction(ProfileActivity.CREATE_PROFILE);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Delete user : onFailure");
                //status.setText(t.getMessage());

            }
        });

    }




}
