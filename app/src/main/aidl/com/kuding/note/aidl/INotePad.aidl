package com.kuding.note.aidl;

interface INotePad
{
    int bindNoteData(int type);
    
    String getTime();
    String getDBText();
    String getSDCardText();
    long getCurrentPosition();
    long getDataLength();
    boolean isNotesItemNull();
    int getType();

    boolean addNewNote();
    void updateNoteWidget(int flag);
}
