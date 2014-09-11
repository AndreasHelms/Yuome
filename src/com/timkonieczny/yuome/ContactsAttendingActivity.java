package com.timkonieczny.yuome;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import com.timkonieczny.yuome.ContactsFragment.MyMenuItemStuffListener;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;



public class ContactsAttendingActivity extends ListActivity {
	
    public static ArrayList<HashMap<String, String>> friends_attending_list = new ArrayList<HashMap<String,String>>();
    public static ChooseContactsAdapter mAdapter;
    public static ProgressDialog dialog = null;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_attending);
        setTitle(R.string.contacts_attending_title);
        
        Thread friends_thread = new FriendsThread();
        friends_thread.start();
        try {
        	long waitMillis = 10000;
        	while (friends_thread.isAlive()) {
        	   friends_thread.join(waitMillis);
        	}
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
        
       if (friends_attending_list != null && friends_attending_list.size() != 0) { 
       
	        mAdapter = new ChooseContactsAdapter(this,
	        		friends_attending_list,
	        		 R.layout.activity_choose_contacts_item,
	                 new String[] {"title"},
	                 new int[] {R.id.title});
	        
	       ListView listView = this.getListView();
	       setListAdapter(mAdapter);
	       //listView.setClickable(true);
	       listView.setFocusableInTouchMode(false);
	       listView.setFocusable(false);
	       listView.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	SaveValue.setSelectedFriendName(friends_attending_list.get(position).get("title"));
	            	Log.i("ContactsFragment", "Friend: " + friends_attending_list.get(position).get("title"));
	    			Fragment fragment = new ContactsDetailFragment();
	    			FragmentManager fragmentManager = getFragmentManager();
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	            }
	        });
        
       }
	}
	
	// Action Bar Button
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_contacts_attending, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	          case R.id.action_accept:
	        	dialog = ProgressDialog.show(ContactsAttendingActivity.this, "","R.string.dialog_contact_handling", true);
	            new Thread(
	            	new Runnable(){
	            		public void run(){
							try {
								PHPConnector.acceptFriends(mAdapter.getCheckedUserIDs());
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            		}
	        		}
	            ).start();
	          	Intent intent = new Intent(this, MainActivity.class);
	        	startActivity(intent);
	            break;
	            
	            
	          case R.id.action_decline:
		        	dialog = ProgressDialog.show(ContactsAttendingActivity.this, "","R.string.dialog_contact_handling", true);
		            new Thread(
		            	new Runnable(){
		            		public void run(){
//								try {
//									PHPConnector.addBuy(article_list, mAdapter.getCheckedUserIDs(), data.getString("storeID"), data.getString("date"), data.getDouble("total"));
//								} catch (ClientProtocolException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
		            		}
		        		}
		            ).start();
//		          	Intent intent = new Intent(this, MainActivity.class);
//		        	startActivity(intent);
		            break;
	          
	          default:
	            break;
        }
	          return true;
  }


	public class FriendsThread extends Thread{
	  	  public void run(){
	  		  try {
	  			  friends_attending_list = PHPConnector.getData("get_friends_attending.php");
	  		  } catch (ClientProtocolException e) {
	  			  // TODO Auto-generated catch block
	  			  e.printStackTrace();
	  		  } catch (IOException e) {
	  			  // TODO Auto-generated catch block
	  			  e.printStackTrace();
	  		  }
	  	  }
		}
	
}
