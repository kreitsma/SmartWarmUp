package com.app.kyle.smartwarmup;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    final Context context = this;
    MediaPlayer mPlayer = new MediaPlayer();
    Boolean playing = false;
    int currentTrack = 0;
    String message;
    String[] fileNames;
    String[] noteNames = new String[] {"c", "c_", "d", "d_", "e", "f",
                                        "f_", "g", "g_", "a", "a_", "b"}; //Index of notes



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the text files from assets
        String[] files = getTextFiles();
        String[] filesNamesOnly = removeFileExtensions(files); //Version of file names to be displayed in the ListView

        //Populate the list view and ready for clicks
        populateListView(filesNamesOnly);
        registerClickCallback();


    }



    private void populateListView(String[] files) {
        MyAdapter adapter = new MyAdapter(MainActivity.this, R.layout.listitem, files);

        ListView list = (ListView) findViewById(R.id.listViewMain);
        list.setAdapter(adapter);
    }


        private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listViewMain);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView textView =  (TextView) viewClicked.findViewById(R.id.menu_text);

                final String file = textView.getText().toString();

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompt.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText octave = (EditText) promptsView
                        .findViewById(R.id.octaveEntry);

                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                final EditText intervals = (EditText) promptsView
                        .findViewById(R.id.intervalEntry);

                final Spinner dropdown = (Spinner) promptsView
                        .findViewById(R.id.noteEntry);
                final String[] listNotes = new String[]{"C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb",
                                                  "G", "G#/Ab", "A", "A#/Bb", "B"};

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, listNotes);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Play",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        int noteIndex = ArrayUtils.indexOf(listNotes, dropdown.getSelectedItem().toString());
                                        imm.hideSoftInputFromInputMethod(octave.getWindowToken(), 0);
                                        playWarmUp(Integer.parseInt(intervals.getText().toString()), noteNames[noteIndex], Integer.parseInt(octave.getText().toString()) + 1, file);
                                        mPlayer.start();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        imm.hideSoftInputFromInputMethod(octave.getWindowToken(), 0);
                                    }
                                });

                // create alert dialog
                final AlertDialog dialog = alertDialogBuilder.create();

                dialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                // show it
                dialog.show();

                //Disable 'Play' button by default
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(false);

                //Set listener for when octave is filled
                octave.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Check if edittext is empty
                        if (TextUtils.isEmpty(s)) {
                            // Disable ok button
                            ((AlertDialog) dialog).getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            if (intervals.getText().toString().matches("")) {
                                String poop = intervals.getText().toString();
                                ((AlertDialog) dialog).getButton(
                                        AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                            else{
                                ((AlertDialog) dialog).getButton(
                                        AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }

                    }
                });

                //Set listener for when interval is filled
                intervals.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Check if edittext is empty
                        if (TextUtils.isEmpty(s)) {
                            // Disable ok button
                            ((AlertDialog) dialog).getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            if (octave.getText().toString().matches("")) {
                                ((AlertDialog) dialog).getButton(
                                        AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                            else{
                                ((AlertDialog) dialog).getButton(
                                        AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }

                    }
                });
            }
        });
    }



    public String[] getTextFiles () {
        try {
            String[] fileList = getAssets().list("warmups");

            fileList = ArrayUtils.removeElement(fileList, "images");
            fileList = ArrayUtils.removeElement(fileList, "sounds");
            fileList = ArrayUtils.removeElement(fileList, "webkit");
            return fileList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] removeFileExtensions(String[] files) { //Only for .txt
        for (int i = 0; i < files.length; i++) {
            files[i] = files[i].substring(0, files[i].length() - 4);
        }
        return files;
    }

    public String getNoteName (String note, int interval) {
        Integer.toString(interval);
        note += interval;

        return note;
    }

    public void playWarmUp (int intervals, String strtNote, int strtOct, String file) {
        Note[] noteStructure = readNotesFromTextFile(file);
        int startIndex = java.util.Arrays.asList(noteNames).indexOf(strtNote); //Index of startNote in noteNames
        String startNoteName;   //e.g. c_6i
        String[] fileNameSegment;  //File names for a single iteration
        int count = 0; //Used in replacement of i in case octave goes up one

        for (int i = 0; i < intervals; i++ ) {
            if (startIndex + count >= noteNames.length) { //Need to increment the octave by 1
                startIndex = (startIndex + count) - noteNames.length; //Reset the start index
                strtOct += 1; //Increment the start Octave
                startNoteName = getNoteName(noteNames[startIndex], strtOct);
                count = 1; //Reset count
            }
            else
            {
                startNoteName = getNoteName(noteNames[startIndex + count], strtOct);
                count++; //Only increment if we have not changed octaves
            }
            //Eventually want to support descending warmups as well; this only works for ascending

            fileNameSegment = getSoundFileSequence(noteStructure, startNoteName);

            fileNames = ArrayUtils.addAll(fileNames, fileNameSegment);
        }
        try {
            mPlayer = MediaPlayer.create(this, getResources().getIdentifier(fileNames[currentTrack], "raw", getPackageName()));
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, "Warmup out of range!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Set the 'playing' text and animate the play bar
        TextView playing = (TextView)findViewById(R.id.playingSong);
        playing.setText(file);


        Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this,
            R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);

        mPlayer.setOnCompletionListener(this);
    }

    public void onCompletion(MediaPlayer arg0) {  //Valentina Chumak of StackOverflow provided this code.
        arg0.release();
        if (currentTrack < (fileNames.length - 1)) {
            currentTrack++;
            try {
                arg0 = MediaPlayer.create(this, getResources().getIdentifier(fileNames[currentTrack], "raw", getPackageName()));
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, "Warmup out of range!", Toast.LENGTH_SHORT).show();
                return;
            }

            arg0.setOnCompletionListener(this);
            arg0.start();
        }
        else {
            Animation bottomDown = AnimationUtils.loadAnimation(MainActivity.this,
                    R.anim.bottom_down);
            ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.hidden_panel);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.INVISIBLE);
            currentTrack = 0; //Reset the sequence
            int l = ArrayUtils.getLength(fileNames);
            for (int i = 0; i < l; i++) {
                fileNames = ArrayUtils.remove(fileNames, 0);
            }
        }
    }

    public Note[] readNotesFromTextFile(String fileName) {
        AssetManager assetManager = getAssets(); //Create assetmanager
        InputStream input;

        Note[] empty = new Note[0]; //Empty array of notes to return if try block fails

        try {
            input = assetManager.open("warmups/" + fileName + ".txt"); //Open file

            int size = input.available(); //Available size?

            byte[] buffer = new byte[size]; //Create new buffer
            input.read(buffer); //Read to buffer?
            input.close();

            String text = new String(buffer); //Assign buffer content to string
            String[] noteVals = text.split(" "); //Split at spaces

            Note[] notes = new Note[noteVals.length]; //Declare array of notes of same length as noteVals
            String[] components; //Array for the split strings
            Integer pitch, duration; //Integers to use when creating the new notes

            for (int i = 0; i < noteVals.length; i++) {
                components = noteVals[i].split("-"); //Split at hyphen and place values into "components"

                pitch = Integer.parseInt(components[0]);
                duration = Integer.parseInt(components[1]);

                notes[i] = new Note(pitch, duration);
            }
            return notes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return empty;
    }

    //CREDIT TO ziad-halabi9 (GitHub) FOR THE PIANO SOUND WAV FILES
    //Underscore in wav files means sharp


    //Convert an array of Notes into an array of the corresponding sound files
    //based on the given starting note.
    public String[] getSoundFileSequence(Note[] notesFromFile, String rootNote){
        String[] noteFileNames = new String[notesFromFile.length];
        int splitIndex = 1; //Used to help find substrings when there may be an underscore which indicates sharp

        if (rootNote.indexOf("_") != -1) { //If rootnote contains a sharp
            splitIndex = 2;                 //Include code here for "negative" notes too - below the tonic - denoted by ! before the number
        }

        String rootNoteName = rootNote.substring(0,splitIndex);
        int rootOctave = Integer.parseInt(rootNote.substring(splitIndex));
        int indexBias = Arrays.asList(noteNames).indexOf(rootNoteName);

        Hashtable<Integer, String> numberToDuration = new Hashtable<Integer, String>() {{ put(1, "w"); put(2, "h");
                                                                                          put(4, "q"); put(8, "i"); put(16, "s"); }};

        for (int i = 0; i < notesFromFile.length; i++) {
            if (notesFromFile[i].getPitch() + indexBias < noteNames.length) { //If current note does not go to a higher octave number
                noteFileNames[i] = noteNames[notesFromFile[i].getPitch() + indexBias]
                        + rootOctave + numberToDuration.get(notesFromFile[i].getDuration());
            }
            else if (notesFromFile[i].getPitch() + indexBias >= noteNames.length) { //Current note goes into next octave up
                if (notesFromFile[i].getPitch() + indexBias >= noteNames.length * 2) { //Current note goes 2 octave numbers up
                    noteFileNames[i] = noteNames[(notesFromFile[i].getPitch() + indexBias) - 24] //Reset the note index
                            + (rootOctave + 2) + numberToDuration.get(notesFromFile[i].getDuration()); //But add 2 to the octave
                }
                else { //Current note goes one octave number up
                    noteFileNames[i] = noteNames[(notesFromFile[i].getPitch() + indexBias) - 12] //Reset the note index
                            + (rootOctave + 1) + numberToDuration.get(notesFromFile[i].getDuration()); //But add 1 to the octave
                }
            } //Eventually want to support notes that go an octave number below


        }

        return noteFileNames;
    }

    public class MyAdapter extends ArrayAdapter {
        public MyAdapter(Context context, int textViewResourceId, String[] files) {
            super(context, textViewResourceId, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.listitem, parent, false);
            final TextView tv = (TextView)rowView.findViewById(R.id.menu_text);
            Button preview = (Button)rowView.findViewById(R.id.preview);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playWarmUp(1, "c", 4, tv.getText().toString());
                    mPlayer.start();
                }
            });
            preview.setFocusable(false);


            Object s = getItem(position);
            tv.setText(s.toString());
            preview.setText("Preview");
            return rowView;
        }
    }
}


