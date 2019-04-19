package com.csg.reamlexam;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csg.reamlexam.databinding.ActivityRealmBinding;
import com.csg.reamlexam.databinding.ItemTwoTextBinding;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ActivityRealmBinding mBinding;
    private Realm mRealm = Realm.getDefaultInstance();
    private PersonAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_realm);

        RealmResults<Person> results = mRealm.where(Person.class)
                .sort("age", Sort.DESCENDING)
                .findAll();

        mAdapter = new PersonAdapter(results);
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRealm.beginTransaction();
                Person user = mRealm.createObject(Person.class);
                user.setName(mBinding.editName.getText().toString());
                user.setAge(Integer.parseInt(mBinding.editAge.getText().toString()));

                mRealm.commitTransaction();
            }
        });

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange: ");
                RealmResults<Person> results = mRealm.where(Person.class)
                        .sort("age", Sort.DESCENDING)
                        .contains("name", s)
                        .findAll();
                // 어댑터 새로 가져오는식X -> update하는식으로
//                PersonAdapter adapter = new PersonAdapter(results);
//                mBinding.recyclerView.setAdapter(adapter);
                mAdapter.updateData(results);
                return true;
            }
        });

        mBinding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String removeName = mBinding.searchView.getQuery().toString();

                mRealm.beginTransaction();
                RealmResults<Person> results1 = mRealm.where(Person.class)
                        .equalTo("name", removeName)
                        .findAll();

                results1.deleteAllFromRealm();
                mRealm.commitTransaction();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();

    }

    private static class PersonAdapter extends RealmRecyclerViewAdapter<Person,PersonAdapter.PersonViewHolder> {

        public PersonAdapter(@Nullable OrderedRealmCollection<Person> data) {
            super(data, true);
        }

        @NonNull
        @Override
        public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_two_text, viewGroup, false);
            return new PersonViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PersonViewHolder personViewHolder, int i) {
            personViewHolder.binding.setPerson(getItem(i));
        }

        public static class PersonViewHolder extends RecyclerView.ViewHolder {
            ItemTwoTextBinding binding;

            public PersonViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTwoTextBinding.bind(itemView);
            }
        }
    }
}
