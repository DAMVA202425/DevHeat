
# DevHeat

Android app to automate GitHub profile edition and README creation.


I used SQLite, SharedPreferences, Internal Storage, XML elements generation...

There are 5 activities:

## MainActivity

Starts the <ins>SQLite database</ins> and loads the users into new <ins>XML Buttons</ins> with theirnames.

When you click on the username, his name and his password are added to the 'login' activity via <ins>'putExtra'</ins> on it's intent.

## New user

You can create new users with a name and a password, and the users will be inserted into the SQLite database.

## login

Gets the extras setted on 'MainActivity' and sets the TextView welcoming the user. If the Username and his password are correct, the next activity will start.

## Template

It starts with one Spinner with several options to add to the Readme generatos (Titles, text, text with a link...)
and a Button. If you click the button, it will generate another <ins>XML Spinner</ins>. You can generate countless Spinners.

3 Buttons on bottom:

 - bntClipoard: Saves the README into the clipboard with the ClipboardManager.
 - btnClearOptions: Deletes the content of the readme by removing all views from the container.
 - btnSave: saves the readme file into the internal storage with <ins>FileOutputStream</ins>, and lets the user choose between the predetermined path or other path, with an AlertDialog.

## Menu

2 Buttons:
 - deleteSP: deletes the SharedPreferences.
 - deleteDB: truncates the database, eliminates all the users.

A Switch:

Lets the user choose between the dark-mode or the light-mode, and stores the option at the <ins>SharedPreferences</ins>.
