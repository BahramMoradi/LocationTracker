package dk.dtu.lbs.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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


/**
 * This class is used for both create and update profile
 */
public class CreateUpdateProfile extends Fragment implements View.OnClickListener {
    private String TAG=CreateUpdateProfile.class.getName();
    private static final String ACTION = "ACTION";
    public static final String  UPDATE="UPDATE";
    public static final String CREATE="CREATE";
    private static final String NAME="USER_NAME";
    private static final String PHONE="USER_PHONE";
    private static final String MAIL="USER_MAIL";
    private static final String DESCRIPTION="USER_DESCRIPTION";
    private OnFragmentInteractionListener mListener;

    private ImageView img=null;
    private EditText name=null;
    private EditText phone=null;
    private EditText mail=null;
    private EditText description=null;
    private Button save=null;
    private TextView status=null;
    private String actionRequestFromActivity =null;
    private Context context=null;
    private TrackerDataSource localDatabase=null;


    /**
     * Required empty public constructor
     */
    public CreateUpdateProfile() {}
    public static CreateUpdateProfile newInstance(String action) {
        CreateUpdateProfile fragment = new CreateUpdateProfile();
        Bundle args = new Bundle();
        args.putString(ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this.getActivity().getApplicationContext();
        localDatabase=TrackerDataSource.getInstance(context);
        if (getArguments() != null) {
           actionRequestFromActivity = getArguments().getString(ACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_create_profile, container, false);
        init(view);
        switch(actionRequestFromActivity){
            case CREATE:
                if(savedInstanceState!=null){
                    name.setText(savedInstanceState.getString(NAME,""));
                    phone.setText(savedInstanceState.getString(PHONE,""));
                    mail.setText(savedInstanceState.getString(MAIL,""));
                    description.setText(savedInstanceState.getString(DESCRIPTION,""));
                }
                break;
            case UPDATE:
                // show the user information
                showProfileInfo();
                break;
        }

        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(actionRequestFromActivity.equalsIgnoreCase(CREATE)){
        outState.putString(NAME,name.getText().toString());
        outState.putString(PHONE,phone.getText().toString());
        outState.putString(MAIL,mail.getText().toString());
        outState.putString(DESCRIPTION,description.getText().toString());
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void requestActivityForAction(String action) {
        if (mListener != null) {
            mListener.onFragmentInteraction(action);
        }
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
        mListener = null;
    }

    public void onClick(View v) {
        if(!AppUtil.isEmailValid(mail.getText())){
            AppUtil.showDialog(this.getActivity(), "Invalid E-Mail", "Please enter a valid mail address");
            return;
        }

        if(AppUtil.isNetworkConnected(this.getActivity())){
            if (actionRequestFromActivity.equalsIgnoreCase(UPDATE)) {
                updateUserProfile();
            }
            if (actionRequestFromActivity.equalsIgnoreCase(CREATE)) {
                createUserProfile();
            }
        }else{
            AppUtil.showNoNetworkDialog(this.getActivity());
        }


    }


    public void init(View view){
        img=(ImageView)view.findViewById(R.id.userImage);
        name=(EditText)view.findViewById(R.id.userNameTF);
        phone=(EditText)view.findViewById(R.id.phoneTF);
        mail=(EditText)view.findViewById(R.id.mailTF);
        description=(EditText)view.findViewById(R.id.descriptionTF);
        save=(Button)view.findViewById(R.id.saveBT);
        save.setOnClickListener(this);
        status=(TextView)view.findViewById(R.id.statusTVFrag);


    }





   public void showProfileInfo(){
       Log.d(TAG,"show profile info profile");
       //List<Profile> profiles=LocalDatabase.getInstance().readProfile(Realm.getInstance(context));
       List<Profile> profiles=localDatabase.readProfile();

       if(!profiles.isEmpty()){
           Profile profile=profiles.get(0);
           name.setText(profile.getName());
           phone.setText(String.valueOf(profile.getPhone()));
           mail.setText(profile.getMail());
           description.setText(profile.getDescription());
       }

   }
    public void updateUserProfile(){
        long uid=0;
        //List<Profile> profiles=LocalDatabase.getInstance().readProfile(Realm.getInstance(context));
        List<Profile> profiles=localDatabase.readProfile();

        if(!profiles.isEmpty()){
           uid= profiles.get(0).getUid();
        }
        try{
            Profile user=new Profile(uid,name.getText().toString(),Long.parseLong(phone.getText().toString(), 10),mail.getText().toString(),description.getText().toString());
            TrackerRestService client= RESTClient.getClient(context);
            Call<Profile> call=client.update(user);
            call.enqueue(new UpdateCallback(user));
        }catch (NumberFormatException nfe){

            showInvalidPhoneNrDialog(phone.getText().toString());


        }





    }
    public void createUserProfile(){
        try {

                Log.d(TAG, "calling websevice");
                Profile user = new Profile(0, name.getText().toString(), Long.parseLong(phone.getText().toString(), 10), mail.getText().toString(), description.getText().toString());
                TrackerRestService client = RESTClient.getClient(context);
                Call<Profile> call = client.create(user);
                call.enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Response<Profile> response, Retrofit retrofit) {
                        int statusCode = response.code();
                        String msg = response.message();
                        Log.d(TAG, "Create profile / Status Cod: " + statusCode + " msg :" + msg);
                        Profile user = response.body();
//                    status.setText("status code :" + statusCode + " message :" + msg + " Body: " + user.getUid() + " " + user.getName());
                        if (statusCode == 200 && user.getUid() != 0) {
                            //saveUser(us);
                            localDatabase.saveProfile(user);
                            requestActivityForAction(ProfileActivity.SHOW_PROFILE);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        status.setText(t.getMessage());

                    }
                });

        }catch(NumberFormatException nfe){
            showInvalidPhoneNrDialog(phone.getText().toString());
        }
    }

    public void showInvalidPhoneNrDialog(String number){
        String title="Invalid phone Nr";
        String msg="You have entered invalid phone number : "+number;
        int icon=R.mipmap.ic_delete;

        final Dialog dialog=new Dialog(this.getActivity());
        View.OnClickListener yes=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };

        View.OnClickListener no=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };

        AppUtil.prepareDialog(dialog,title,msg,icon,yes,no);
        dialog.findViewById(R.id.cancelDialogBT).setEnabled(false);
        dialog.show();

    }




    class UpdateCallback implements Callback<Profile> {
        Profile user=null;
        public UpdateCallback(Profile user){
            this.user=user;
        }
        @Override
        public void onResponse(Response<Profile> response, Retrofit retrofit) {
            int statusCode=response.code();
            String msg=response.message();
            status.setText("status code :"+statusCode+ " message :"+msg);
            if(statusCode==200){
                Log.d(TAG,"updateProfile statusCode : "+statusCode);
                //saveUser(user);
                localDatabase.updateProfile(user);
                requestActivityForAction(ProfileActivity.SHOW_PROFILE);
            }

        }

        @Override
        public void onFailure(Throwable t) {
            Log.d(TAG,"update profile : onFailure");
            status.setText(t.getMessage());
        }
    }
}
