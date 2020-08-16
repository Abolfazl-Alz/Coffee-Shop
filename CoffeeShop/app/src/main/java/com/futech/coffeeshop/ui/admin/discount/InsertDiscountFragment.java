package com.futech.coffeeshop.ui.admin.discount;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.dariushm2.PersianCaldroid.caldroiddialog.PersianCaldroidDialog;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.dialog.ProgressDialog;
import com.futech.coffeeshop.utils.DateHelper;
import com.futech.coffeeshop.utils.DiscountHelper;
import com.futech.coffeeshop.utils.listener.DataChangeListener;

import java.util.Date;

import calendar.PersianDate;

public class InsertDiscountFragment extends Fragment implements DataChangeListener {

    private PersianDate mSelectedDate;
    private Button mChoiceDate;

    private EditText nameText;
    private EditText codeText;
    private EditText valueText;

    private ProgressDialog progressDlg;

    public InsertDiscountFragment() {
        mSelectedDate = new PersianDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insert_discount, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChoiceDate = view.findViewById(R.id.choice_date_btn);
        mChoiceDate.setOnClickListener(v -> showSelectDateDialog());

        nameText = view.findViewById(R.id.name_text);
        codeText = view.findViewById(R.id.code_text);
        valueText = view.findViewById(R.id.value_text);

        progressDlg = new ProgressDialog(requireContext());
        setHasOptionsMenu(true);

        if (getActivity() != null && getActivity() instanceof DiscountControlActivity) {
            ((DiscountControlActivity) getActivity()).setActionBarColor(R.color.dark_primary_dark);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem addMenu = menu.add(R.string.add_menu);
        addMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        addMenu.setOnMenuItemClickListener(v -> {
            addDiscount();
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addDiscount() {
        DiscountHelper discount = new DiscountHelper(requireContext());
        String name = nameText.getText().toString().trim();
        String code = codeText.getText().toString().trim();
        int value = Integer.parseInt(valueText.getText().toString());
        Date date = DateHelper.convertPersianDateToGregorianDate(mSelectedDate);
        discount.addNewDiscount(name, code, value, date, this);
        progressDlg.setTitle(R.string.adding_data);
        progressDlg.setInformation("");
        progressDlg.showDialog();
    }

    private void showSelectDateDialog() {
        PersianCaldroidDialog persianCaldroidDialog = new PersianCaldroidDialog().setOnDateSetListener((dialog, date) -> {
            dialog.dismiss();
            if (date.after(new PersianDate())) {
                mSelectedDate = date;
                mChoiceDate.setText(date.toString());
            }else {
                Toast.makeText(getContext(), R.string.future_error_select_date, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.future_date_dlg_msg);
                builder.setTitle(R.string.incorrect_date);
                builder.setIcon(android.R.drawable.stat_sys_warning);
                builder.setPositiveButton(R.string.choice_again, (dialog1, which) -> showSelectDateDialog());
                builder.setNegativeButton(R.string.cancel_dlg, (dialog1, which) -> dialog1.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        persianCaldroidDialog.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.font_b_yekan));
        persianCaldroidDialog.setSelectedDate(mSelectedDate);
        persianCaldroidDialog.show(requireActivity().getSupportFragmentManager(), PersianCaldroidDialog.class.getName());
    }

    @Override
    public void onChange() {
        if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
        else
            NavHostFragment.findNavController(InsertDiscountFragment.this).navigate(R.id.action_InsertDiscountFragment_to_ListDiscountFragment);
        progressDlg.dismiss();
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        progressDlg.dismiss();
    }
}
