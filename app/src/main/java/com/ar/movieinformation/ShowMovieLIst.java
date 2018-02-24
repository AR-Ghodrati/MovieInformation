package com.ar.movieinformation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ShowMovieLIst extends AppCompatActivity {
    ListView Movielist;
    String Movie_String_data_HTMLsave="";
    int Check=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);
        Movielist = (ListView) findViewById(R.id.Movielist);
        //ReadData();
    }

   /* private void ReadData() {
        Thread tread = new Thread(new Runnable() {
            @Override
            public void run() {
                database db = new database(getApplicationContext(), "Movie_db", null, 1);
                SQLiteDatabase temp = db.getReadableDatabase();
                int counter = 0;
                String Movie_Rank,Movie_farsiname, Movie_year, Movie_name, MovieDirector_name, Movie_IMDB_link, Movie_picture_link, IMDB_RANK_simple, Actors_Actress, IMDB_RANK_full;
                final Vector<Movie>movies=new Vector<Movie>();
                String[] MOVIE = new String[250];
                Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name","Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
                cursor.moveToFirst();
                cursor.moveToNext();
                while (!cursor.isAfterLast()) {
                    Movie tempMovie=new Movie();
                    Movie_name = cursor.getString(cursor.getColumnIndex("Movie_name"));
                    Movie_Rank = cursor.getString(cursor.getColumnIndex("Movie_Rank"));
                    Movie_year = cursor.getString(cursor.getColumnIndex("Movie_year"));
                    Movie_farsiname=cursor.getString(cursor.getColumnIndex("Movie_farsi_name"));
                    MovieDirector_name = cursor.getString(cursor.getColumnIndex("MovieDirector_name"));
                    Movie_IMDB_link = cursor.getString(cursor.getColumnIndex("Movie_IMDB_link"));
                    Movie_picture_link = cursor.getString(cursor.getColumnIndex("Movie_picture_link"));
                    IMDB_RANK_simple = cursor.getString(cursor.getColumnIndex("IMDB_RANK_simple"));
                    Actors_Actress = cursor.getString(cursor.getColumnIndex("Actors_Actress"));
                    IMDB_RANK_full = cursor.getString(cursor.getColumnIndex("IMDB_RANK_full"));
                    tempMovie.setMovie_name(Movie_name);
                    tempMovie.setMovie_Rank(Movie_Rank);
                    tempMovie.setMovie_farsi_name(Movie_farsiname);
                    tempMovie.setMovie_IMDB_link(Movie_IMDB_link);
                    tempMovie.setMovie_picture_link(Movie_picture_link);
                    tempMovie.setIMDB_RANK_simple(IMDB_RANK_simple);
                    tempMovie.setActors_Actress(Actors_Actress);
                    tempMovie.setIMDB_RANK_full(IMDB_RANK_full);
                    tempMovie.setMovieDirector_name(MovieDirector_name);
                    tempMovie.setMovie_year(Movie_year);
                    tempMovie.setMovie_rank(""+(counter+1));
                    movies.addElement(tempMovie);
                    counter++;
                    cursor.moveToNext();
                }
                if(Check==0)
                {
                    for (int i = 0; i < movies.size(); i++) {
                          MOVIE[i]=(i+1)+" -"+movies.elementAt(i).getMovie_name();
                    }
                }
               else if (Check == 1) {
                    for (int i = 0; i < 249; i++)
                        for (int j = i + 1; j < 250; j++)
                            if (movies.elementAt(j).getMovie_year().compareToIgnoreCase(movies.elementAt(i).getMovie_year()) > 0) {
                                Movie t = movies.elementAt(i);
                                movies.setElementAt(movies.elementAt(j),i);
                                movies.setElementAt(t,j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        MOVIE[i]=(i+1)+" -"+movies.elementAt(i).getMovie_name();
                    }
                }
                else if (Check == 2)
                {
                    for (int i = 0; i < 250; i++)
                        for (int j = i + 1; j < 250; j++)
                            if (movies.elementAt(j).getMovieDirector_name().compareToIgnoreCase(movies.elementAt(i).getMovieDirector_name()) < 0) {
                                Movie t = movies.elementAt(i);
                                movies.setElementAt(movies.elementAt(j),i);
                                movies.setElementAt(t,j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        MOVIE[i]=(i+1)+" -"+movies.elementAt(i).getMovie_name();
                    }
                }
                else if(Check==3)
                {
                    for (int i = 0; i < 250; i++)
                        for (int j = i + 1; j < 250; j++)
                        if(movies.elementAt(j).countranking()>movies.elementAt(i).countranking())
                        {
                            Movie t = movies.elementAt(i);
                            movies.setElementAt(movies.elementAt(j),i);
                            movies.setElementAt(t,j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        MOVIE[i]=(i+1)+" -"+movies.elementAt(i).getMovie_name();
                    }
                }
                else if(Check==4)
                {
                    for (int i = 0; i < 250; i++)
                        for (int j = i + 1; j < 250; j++)
                            if(movies.elementAt(j).getMovie_name().compareToIgnoreCase(movies.elementAt(i).getMovie_name())<0)
                            {
                                Movie t = movies.elementAt(i);
                                movies.setElementAt(movies.elementAt(j),i);
                                movies.setElementAt(t,j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        MOVIE[i]=(i+1)+" -"+movies.elementAt(i).getMovie_name();
                    }
                }

                temp.close();
                db.close();
                ArrayAdapter<String> arry = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, MOVIE);
                Movielist.setAdapter(arry);
               Movielist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent gotopage = new Intent(ShowMovieLIst.this, fullmovieinfo.class);
                       // Intent gotopagefull = new Intent(ShowMovieLIst.this, ShowFullInfofull.class);
                        gotopage.putExtra("id",movies.elementAt(position).getMovie_rank());
                        gotopage.putExtra("name","");
                        gotopage.putExtra("Movie_name_farsi",movies.elementAt(position).getMovie_name_farsi());
                        gotopage.putExtra("Movie_name",movies.elementAt(position).getMovie_name());
                        gotopage.putExtra("Movie_Rank",movies.elementAt(position).getMovie_Rank());
                        gotopage.putExtra("Movie_year",movies.elementAt(position).getMovie_year());
                        gotopage.putExtra("MovieDirector_name",movies.elementAt(position).getMovieDirector_name());
                        gotopage.putExtra("Movie_IMDB_link",movies.elementAt(position).getMovie_IMDB_link());
                        gotopage.putExtra("Movie_picture_link",movies.elementAt(position).getMovie_picture_link());
                        gotopage.putExtra("IMDB_RANK_simple",movies.elementAt(position).getIMDB_RANK_simple());
                        gotopage.putExtra("Actors_Actress",movies.elementAt(position).getActors_Actress());
                        gotopage.putExtra("IMDB_RANK_full",movies.elementAt(position).getIMDB_RANK_full());
                        gotopage.putExtra("count",""+movies.elementAt(position).countranking());


                        //else
                            startActivity(gotopage);
                    }
                });
            }
        });
        tread.run();
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_1,menu);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.UpdateList)
        {
            Intent intent=new Intent(this,DownloadData.class);
            startActivity(intent);
        }
        else if(id==R.id.SortYears) {
            Check = 1;
          //  ReadData();
        }
        else if(id==R.id.SortRank) {
            Check = 0;
          //  ReadData();
        }
        else if(id==R.id.SortpeopleRanking) {
            Check = 3;
         //   ReadData();
        }
        else if(id==R.id.SortNames)
        {
            Check=4;
         //   ReadData();
        }
        return super.onOptionsItemSelected(item);
    }
}