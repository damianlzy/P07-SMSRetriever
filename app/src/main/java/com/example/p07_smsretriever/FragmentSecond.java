package com.example.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {


    Button btnRetrieveSMS2;
    Button btnEmail;
    EditText etWord;
    TextView tvSMS2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);

        etWord = (EditText) view.findViewById(R.id.etWord);
        btnRetrieveSMS2 = (Button) view.findViewById(R.id.btnRetrieveSMS2);
        tvSMS2 = (TextView) view.findViewById(R.id.tvSMS2);
        btnEmail = (Button) view.findViewById(R.id.btnEmail);

        btnRetrieveSMS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }
                // Create all messages URI
                Uri uri = Uri.parse("content://sms");

                // The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                // The filter String
                String filter="";

                // The matches for the ?

                String word = etWord.getText().toString();
                String split[] = word.split(" ");

                for(int i = 0; i<split.length; i++){
                    filter += "body LIKE ?";
                    String arg = "%"+split[i]+"%";
                    split[i] = arg;
                    if(i+1 != split.length){
                        filter += " OR ";
                    }
                }



                // Fetch SMS Message from Built-in Content Provider

                Cursor cursor = cr.query(uri, reqCols, filter, split, null);

                String smsBody = "";

                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSMS2.setText(smsBody);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Send email", "");
                String[] TO = {"jason_lim@rp.edu.sg"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SMS");
                emailIntent.putExtra(Intent.EXTRA_TEXT, tvSMS2.getText().toString());

                emailIntent.setType("message/rfc822");

                startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
            }
        });

        return view;
    }
}
