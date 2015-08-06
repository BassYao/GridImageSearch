package com.example.bass.gridimagesearch.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.bass.gridimagesearch.R;
import com.example.bass.gridimagesearch.models.SearchFilter;



public class EditFilterFragment extends DialogFragment {
    SearchFilter filter;
    private OnFilterSaveListener mListener;
    public static EditFilterFragment newInstance(SearchFilter filter) {
        EditFilterFragment fragment = new EditFilterFragment();
        Bundle args = new Bundle();
        args.putSerializable("filter", filter);
        fragment.setArguments(args);
        return fragment;
    }

    public EditFilterFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFilterSaveListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter = (SearchFilter)getArguments().getSerializable("filter");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_filter, container, false);

        Spinner spSize  = (Spinner) v.findViewById(R.id.spSize);
        Spinner spColor = (Spinner) v.findViewById(R.id.spColor);
        Spinner spType  = (Spinner) v.findViewById(R.id.spType);
        EditText edtSite  = (EditText) v.findViewById(R.id.etSite);
        Button btSave  = (Button) v.findViewById(R.id.btSave);

// Create an ArrayAdapter using the string array and a self defined item layout(text align to right)
        ArrayAdapter<CharSequence> aSize = ArrayAdapter.createFromResource(getActivity(),
                R.array.sizes_array, R.layout.spinner_right_item);
        ArrayAdapter<CharSequence> aColor = ArrayAdapter.createFromResource(getActivity(),
                R.array.colors_array, R.layout.spinner_right_item);
        ArrayAdapter<CharSequence> aType = ArrayAdapter.createFromResource(getActivity(),
                R.array.types_array, R.layout.spinner_right_item);
// Specify the layout to use when the list of choices appears
        aSize.setDropDownViewResource(R.layout.spinner_right_item);
        aColor.setDropDownViewResource(R.layout.spinner_right_item);
        aType.setDropDownViewResource(R.layout.spinner_right_item);

// Apply the adapter to the spinner
        spSize.setAdapter(aSize);
        spColor.setAdapter(aColor);
        spType.setAdapter(aType);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    Dialog d = getDialog();
                    filter.color = ((Spinner) d.findViewById(R.id.spColor)).getSelectedItem().toString();
                    filter.type = ((Spinner) d.findViewById(R.id.spType)).getSelectedItem().toString();
                    filter.size = ((Spinner) d.findViewById(R.id.spSize)).getSelectedItem().toString();
                    filter.site = ((EditText) d.findViewById(R.id.etSite)).getText().toString();
                    mListener.onFilterSave(filter);
                    dismiss();
                }
            }
        });
        int iSize  = aSize.getPosition(filter.size);
        int iColor = aColor.getPosition(filter.color);
        int iType  = aType.getPosition(filter.type);
        //set default value from old filter
        spSize.setSelection(iSize);
        spColor.setSelection(iColor);
        spType.setSelection(iType);
        edtSite.setText(filter.site);

        getDialog().setTitle(getString(R.string.filter_title));
//

        return v;

    }


    public interface OnFilterSaveListener {
        public void onFilterSave(SearchFilter newFilter);
    }


}
