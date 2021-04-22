package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteapp.auth.Login;
import com.example.noteapp.auth.Register;
import com.example.noteapp.auth.Splash;
import com.example.noteapp.model.Adapter;
import com.example.noteapp.model.Note;
import com.example.noteapp.note.AddNote;
import com.example.noteapp.note.EditNote;
import com.example.noteapp.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Context context;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    TextView notetitle,notecontent,date;
    NavigationView nav_view;
    RecyclerView  noteLists;
    Adapter adapter;
    FirebaseFirestore fstore;
    FirestoreRecyclerAdapter<Note,NoteViewHolder>noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notetitle =findViewById(R.id.titles);
       // notecontent =findViewById(R.id.content);

        fstore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();


        Query query=fstore.collection("notes").document(user.getUid()).collection("mynotes").orderBy("title", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Note> allnotes=new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class)
                .build();

        noteAdapter=new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allnotes) {
            @SuppressLint("ResourceType")
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {

                noteViewHolder.notetitle.setText(note.getTitle());
                noteViewHolder.notecontent.setText(note.getContent());



                Calendar calendar=Calendar.getInstance();
                final String currentdate= DateFormat.getDateInstance().format(calendar.getTime());

                final Integer code=getRandomcoloer();
                final String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();
                 noteViewHolder.date.setText(currentdate);
                noteViewHolder.mcardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code,null));
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i =new Intent(view.getContext(), NoteDetails.class);
                        i.putExtra("title",note.getTitle());
                        i.putExtra("content",note.getContent());
                        i.putExtra("color",code);

                      //for update you should have id of document
                        i.putExtra("noteId",docId);
                        i.putExtra("date",currentdate);


                        view.getContext().startActivity(i);

                    }
                });

                //for popupmenu to make choice of note(delete,edite share,...etc
                ImageView menuIcon=noteViewHolder.view.findViewById(R.id.menuIcon);
           //     menuIcon.setBackgroundColor(Integer.parseInt("#FFFFF"));


                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu=new PopupMenu(view.getContext(),view);

                        menu.setGravity(GravityCompat.END);


                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Intent i =new Intent(view.getContext(), EditNote.class);
                                i.putExtra("title",note.getTitle());
                                i.putExtra("content",note.getContent());
                                i.putExtra("noteId",docId);

                                startActivity(i);


                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                DocumentReference docref=fstore.collection("notes").document(user.getUid()).collection("mynotes").document(docId);
                                docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error in Deleting Note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });



            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);


                return new NoteViewHolder(view);
            }
        };



        drawerLayout=findViewById(R.id.drawes);
        nav_view=findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        noteLists=findViewById(R.id.noteLists);


       Toolbar toolbar=findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();



        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(noteAdapter);


        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddNote.class));
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.shareapp:
                notecontent =findViewById(R.id.content);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //sendIntent.putExtra(Intent.EXTRA_PACKAGE_NAME,notecontent.getText().toString());

                sendIntent.putExtra(Intent.EXTRA_TEXT,notecontent .getText().toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_to)));
                return (true);


            case R.id.notes:
                //startActivity(new Intent(this,MainActivity.class));
              //  overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;

            case R.id.addnote:
                startActivity(new Intent(this,AddNote.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;

            case R.id.Sync:
                if(user.isAnonymous()) {

                    startActivity(new Intent(this, Login.class));
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

                }else {

                    Toast.makeText(this, "Your Are Connected.", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.logout:

                checkuser();

                break;

            default:
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void checkuser() {
        //if user is real or not
        if (user.isAnonymous()){
            displayAlert();
        }else {
           FirebaseAuth.getInstance().signOut();
           startActivity(new Intent(getApplicationContext(), Splash.class));
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            finish();
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning=new AlertDialog.Builder(this)
                .setTitle("Are you Sure ?")
                .setMessage("you Are Logged in With Temporary Account, logging out will Delete All the Notes")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //delet all the notes created by the Anon usre

                        //delet the user


                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),Splash.class));
                                finish();
                            }
                        });

                    }
                });

        warning.show();

    }


/////////=======================//////////////////=============================////////////////////


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      //  if (item.getItemId()==R.id.settings)
       //     Toast.makeText(this, "menu settings is clicked", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    private class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView notetitle,notecontent,date;
        View view;
        CardView mcardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle =itemView.findViewById(R.id.titles);
            notecontent =itemView.findViewById(R.id.content);
            date =itemView.findViewById(R.id.date);
            mcardView=itemView.findViewById(R.id.notecard);
            view=itemView;
        }
    }
    private int getRandomcoloer() {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.blue);
        colorcode.add(R.color.yellow);
        colorcode.add(R.color.lightepurple);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.greenlighte);
        colorcode.add(R.color.notgreen);

        Random randomcolor=new Random();
        int number =randomcolor.nextInt(colorcode.size());
        return colorcode.get(number);
    }

    @Override
    protected void onStart() {

        super.onStart();
        noteAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter !=null){
            noteAdapter.stopListening();
        }
    }






}