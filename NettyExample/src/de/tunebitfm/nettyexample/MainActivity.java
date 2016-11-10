package de.tunebitfm.nettyexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.tunebitfm.nettyexample.client.WebsocketClient;
import de.tunebitfm.nettyexample.telnet.TelnetClient;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final WebsocketClient client = new WebsocketClient();
//        client.connect();
        
        final TelnetClient client = new TelnetClient();
        client.connect();
        
        final EditText textField = (EditText) this.findViewById(R.id.text_field);

        Button sendButton = (Button) this.findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String message = textField.getText().toString();
                Log.i("websocket-test", "Sending message: " + message);
                client.sendMessage(message);
            }
        });

    }

}
