package com.joeymejias.hangman;

/**
 * Created by jmejias14 on 7/25/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Console;

public class Prompter {
    private Context context;
    private Game mGame;

    public Prompter (Context context) {
        this.context = context;
        String s = getString("Mystery word", "Please type the mystery word");
        mGame = new Game(s.toLowerCase());
    }

    public void play() {
        while (mGame.getRemainingTries() > 0 && !mGame.isSolved()) {
            displayProgress();
            promptForGuess();
        }

        String s;
        if (mGame.isSolved()) {
            /*System.out.printf("Congradulations you won with %d tries remaining. \n",
                    mGame.getRemainingTries());*/

            s = String.format("Congradulations, you won with %d tries remaining. \n",
                    mGame.getRemainingTries());

        } else {
            /*System.out.printf("Bummer the word was %s.  :( \n",
                    mGame.getAnswer());*/
            s = String.format("Bummer the word was %s.  :( \n",
                    mGame.getAnswer());
        }
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        toast.show();
    }

    public boolean promptForGuess() {
        Console console = System.console();
        boolean isHit = false;
        boolean isValidGuess = false;
        while (! isValidGuess) {
            String guessAsString = getString("Letter", "Enter a letter.");//console.readLine("Enter a letter:   ");

            try {
                isHit = mGame.applyGuess(guessAsString);
                isValidGuess = true;
            } catch (IllegalArgumentException iae) {
                //console.printf("%s. Please try gain. \n", iae.getMessage());
                String s = String.format("%s. Please try gain. \n", iae.getMessage());
                Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
                toast.show();
            }
        }


        return isHit;
    }

    public void displayProgress() {
        /*
        System.out.printf("You have %d tries left to solve: %s\n",
                mGame.getRemainingTries(),
                mGame.getCurrentProgress());
        */
        String s = String.format("You have %d tries left to solve: %s\n",
                mGame.getRemainingTries(),
                mGame.getCurrentProgress());

        Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        toast.show();
    }

    //The handleMessage method of this object will be called when we call the sendMessage method of
    //this object.  It throws an exception to break us out of the infinite loops below.
    //When called from get, it also carries the String that the user typed in.

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message.obj == null) {
                throw new RuntimeException();
            }
            throw new RuntimeException(message.obj.toString());
        }
    }

    private MyHandler handler = new MyHandler();

    private String get(String title, String message, int inputType) {
        //A builder object can create a dialog object.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        //This inflater reads the dialog.xml and creates the objects described therein.
        //Pass null as the parent view because it's going in the dialog layout.
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog, null);
        EditText editText = (EditText)view.findViewById(R.id.editText);
        editText.setInputType(inputType);
        builder.setView(view);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText editText = (EditText)v;
                    Editable editable = editText.getText();
                    //Sending this message will break us out of the loop below.
                    Message message = handler.obtainMessage();
                    message.obj = editable.toString();
                    handler.sendMessage(message);
                }
                return false;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //Loop until the user presses the EditText's Done button.
        String retVal = "";
        try {
            Looper.loop();
        } catch (RuntimeException runtimeException) {
            retVal = runtimeException.getMessage();
        }
        alertDialog.dismiss();
        return retVal;
    }

    private int getInt(String title, String message) {
        String s = get(title, message, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException numberFormatException) {
            Toast.makeText(context, "bad int " + s, Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    private long getLong(String title, String message) {
        String s = get(title, message, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException numberFormatException) {
            Toast.makeText(context, "bad long " + s, Toast.LENGTH_LONG).show();
            return 0L;
        }
    }

    private boolean getBoolean(String title, String message) {
        String s = get(title, message, InputType.TYPE_CLASS_TEXT);
        //Boolean.parseBoolean() does not throw any exception.
        if (s.equals("true")) {
            return true;
        }
        if (!s.equals("false")) {
            Toast.makeText(context, "bad bool " + s, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private float getFloat(String title, String message) {
        String s = get(title, message,
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException numberFormatException) {
            Toast.makeText(context, "bad float " + s, Toast.LENGTH_LONG).show();
            return 0f;
        }
    }

    private double getDouble(String title, String message) {
        String s = get(title, message,
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException numberFormatException) {
            Toast.makeText(context, "bad double " + s, Toast.LENGTH_LONG).show();
            return 0d;
        }
    }

    private String getString(String title, String message) {
        //No such thing as a bad String.
        return get(title, message, InputType.TYPE_CLASS_TEXT);
    }

    private void display(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Sending this message will break us out of the loop below.
                Message message = handler.obtainMessage();
                handler.sendMessage(message);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //Loop until the user presses the EditText's Done button.
        try {
            Looper.loop();
        } catch (RuntimeException runtimeException) {
        }
    }

    private void display(String title, int i) {
        display(title, Integer.toString(i));
    }

    private void display(String title, long l) {
        display(title, Long.toString(l));
    }

    private void display(String title, boolean b) {
        display(title, Boolean.toString(b));
    }

    private void display(String title, float f) {
        display(title, Float.toString(f));
    }

    public void display(String title, double d) {
        display(title, Double.toString(d));
    }

}
