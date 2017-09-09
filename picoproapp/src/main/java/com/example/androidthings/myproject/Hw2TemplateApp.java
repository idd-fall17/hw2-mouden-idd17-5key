package com.example.androidthings.myproject;

import com.google.android.things.pio.Gpio;

/**
 * Template for IDD Fall 2017 HW2 (text entry device)
 * Created by bjoern on 9/5/17.
 *
 * Project code for IDD Fall 2017 HW2
 * Created by Michael Oudenhoven on 9/6/17
 */

public class Hw2TemplateApp extends SimplePicoPro {

    //button pin numbers
    private Gpio leftButton = GPIO_128;
    private Gpio upButton = GPIO_39;
    private Gpio rightButton = GPIO_37;
    private Gpio downButton = GPIO_35;
    private Gpio spaceButton = GPIO_34;
    private Gpio enterButton = GPIO_33;

    //2D alphabet array
    private char[][] lowercaseAlpha = {{'a', 'b', 'c', 'd', 'e', 'f', '<'}, {'g', 'h', 'i', 'j', 'k', 'l', '^'}, {'m', 'n', 'o', 'p', 'q', 'r', 's'}, {'t', 'u', 'v', 'w', 'x', 'y', 'z'}};
    private char[][] uppercaseAlpha = {{'A', 'B', 'C', 'D', 'E', 'F', '<'}, {'G', 'H', 'I', 'J', 'K', 'L', '^'}, {'M', 'N', 'O', 'P', 'Q', 'R', 'S'}, {'T', 'U', 'V', 'W', 'X', 'Y', 'Z'}};

    //current character selected
    private int selectedRow = 0;
    private int selectedCol = 0;
    
    //user text input
    private String userInput = "";

    //boolean to tell whether the shift button is pressed or not
    private boolean shift = false;

    //boolean to know wheter or not a button click should be allowed
    private boolean buttonsEnabled = true;
    private Gpio buttonCurrPressed = null;


    @Override
    public void setup() {
        //set up pins to the correct inputs
        pinMode(leftButton, Gpio.DIRECTION_IN);
        pinMode(upButton, Gpio.DIRECTION_IN);
        pinMode(rightButton, Gpio.DIRECTION_IN);
        pinMode(downButton, Gpio.DIRECTION_IN);
        pinMode(spaceButton, Gpio.DIRECTION_IN);
        pinMode(enterButton, Gpio.DIRECTION_IN);

        //set up edge triggers -- only rising, falling taken care of by loop
        setEdgeTrigger(leftButton, Gpio.EDGE_FALLING);
        setEdgeTrigger(upButton, Gpio.EDGE_FALLING);
        setEdgeTrigger(rightButton, Gpio.EDGE_FALLING);
        setEdgeTrigger(downButton, Gpio.EDGE_FALLING);
        setEdgeTrigger(spaceButton, Gpio.EDGE_FALLING);
        setEdgeTrigger(enterButton, Gpio.EDGE_FALLING);

        //print out keyboard and initial empty text
        printKeyboardAndText();
    }

    @Override
    public void loop() {
        //use the loop to detect buttons pressed down or not pressed down
        if (buttonCurrPressed != null){
            //check if button is released
            if(digitalRead(buttonCurrPressed)== HIGH){
                //if the button is now released re-enable the buttons
                buttonsEnabled = true;
                buttonCurrPressed = null;
            }
        }
    }

    @Override
    void digitalEdgeEvent(Gpio pin, boolean value) {
        println("digitalEdgeEvent"+pin+", "+value);
        if(buttonsEnabled) {
            //button currently pressed when the buttons are enabled
            buttonCurrPressed = pin;

            //a button is being pressed down -- disable all other buttons
            buttonsEnabled = false;

            //if statements through all of the buttons to see which was pressed so the selected row
            //and column can be updated
            if(pin == leftButton && value == LOW) {
                //check if can move that direction
                if(selectedCol-1 < 0) {
                    //can't move
                    return;
                }
                //can move
                else {
                    //increment row to the right by one
                    selectedCol -= 1;
                }
            }
            else if (pin == upButton && value == LOW) {
                //check if can move that direction
                if(selectedRow-1 < 0) {
                    //can't move
                    return;
                }
                //can move
                else {
                    //increment row to the right by one
                    selectedRow -= 1;
                }
            }
            else if (pin == rightButton && value == LOW) {
                //check if can move that direction
                if(selectedCol+1 >= lowercaseAlpha[0].length) {
                    //can't move
                    return;
                }
                //can move
                else {
                    //increment row to the right by one
                    selectedCol += 1;
                }
            }
            else if (pin == downButton && value == LOW) {
                //check if can move that direction
                if(selectedRow+1 >= lowercaseAlpha.length) {
                    //can't move
                    return;
                }
                //can move
                else {
                    //increment row to the right by one
                    selectedRow += 1;
                }
            }
            else if (pin == enterButton && value == LOW) {
                //check if shift or backspace character was clicked
                if(uppercaseAlpha[selectedRow][selectedCol] == '<'){
                    //backspace clicked -- remove last character
                    backspace();
                }
                else if(uppercaseAlpha[selectedRow][selectedCol] == '^'){
                    //shift clicked - toggle on and off
                    if(!shift) {
                        shift = true;
                    }
                    else {
                        shift = false;
                    }
                }
                //text entry clicked
                else {
                    if(shift) {
                        userInput += Character.toString(uppercaseAlpha[selectedRow][selectedCol]);
                        //turn off shift
                        shift = false;
                    }
                    else {
                        userInput += Character.toString(lowercaseAlpha[selectedRow][selectedCol]);
                    }
                }
            }
            else if (pin == spaceButton && value == LOW) {
                userInput += " ";
            }
        }

        //print out the keyboard after changes
        printKeyboardAndText();

    }


    /**
     * Puts together a large string to print out to the screen that includes the currently
     * selected character and the string being typed out. Uses HTML formatting to bold the character
     * and for line breaks. The printStringToScreen() has been updated to print HTML strings
     */
    private void printKeyboardAndText() {

        //start with the user input and then two new new lines to get keyboard away from entry
        String toPrint = userInput + "<br><br>";

        String keyboard = "";

        for(int i=0; i<lowercaseAlpha.length; i++){

            String keyboardLine = "";

            for(int j=0; j<lowercaseAlpha[i].length; j++){
                if(!shift) {
                    char currChar = lowercaseAlpha[i][j];

                    //if it is the selected character put stars around it to show user
                    if (i==selectedRow && j==selectedCol) {
                        //add the character and a tab after to space out -- special case for '<' due to html formatting
                        if(currChar == '<'){
                            String backspaceString = "<";
                            keyboardLine += "<b><u><font color=\"red\">"+backspaceString+" </font></u></b>\t\t";
                        }
                        else {
                            keyboardLine += "<b><u><font color=\"red\">" + Character.toString(currChar) + "</font></u></b>\t\t";
                        }

                    }
                    //not selected character just add to keyboard
                    else {
                        //add the character and a tab after to space out
                        keyboardLine += Character.toString(currChar) + "\t\t";
                    }
                }
                //shift on need uppercase
                else {
                    char currChar = uppercaseAlpha[i][j];

                    //if it is the selected character put stars around it to show user
                    if (i==selectedRow && j==selectedCol) {
                        //add the character and a tab after to space out -- special case for '<' due to html formatting
                        if(currChar == '<'){
                            String backspaceString = "<";
                            keyboardLine += "<b><u><font color=\"red\">"+backspaceString+" </font></u></b>\t\t";
                        }
                        else {
                            keyboardLine += "<b><u><font color=\"red\">" + Character.toString(currChar) + "</font></u></b>\t\t";
                        }
                    }
                    //not selected character just add to keyboard
                    else {
                        //add the character and a tab after to space out
                        keyboardLine += Character.toString(currChar) + "\t\t";
                    }
                }
            }
            //add newline character after each row
            keyboard += keyboardLine + "<br>";
        }



        //add keyboard to current user input
        toPrint += keyboard;

        //clear the old text and print the new text
        clearStringOnScreen();
        printStringToScreen(toPrint);
    }

    /**
     * Backspaces a character from the user input
     */
    private void backspace() {
        //make sure there is a character to delete
        if (userInput.length() >= 1) {
            userInput = userInput.substring(0, userInput.length()-1);
        }
    }

}
