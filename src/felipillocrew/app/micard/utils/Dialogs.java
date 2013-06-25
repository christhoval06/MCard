package felipillocrew.app.micard.utils;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

public class Dialogs {
	private Activity app = null;
	
	public Dialogs(Activity app) {
		this.app = app;
	}
	
	public void alert(String...  data) {
		String titulo = data[0],
				msg = data[1];
		final String action = data[2];
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(app);
		alertDialog.setTitle(titulo);
		alertDialog.setMessage(msg);
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog,int which) {
            	dialog.cancel();
            	if(action != null){
            		app.finish();
            	}
            }
        });
		alertDialog.show();
	}
	
	public void alert(String titulo, String msg, DialogInterface.OnClickListener callback) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(app);
		alertDialog.setTitle(titulo);
		alertDialog.setMessage(msg);
		alertDialog.setPositiveButton("OK", callback);
		alertDialog.show();
	}
	
	public void notification(String titulo, String msg, DialogInterface.OnClickListener okcallback) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(app);
		alertDialog.setTitle(titulo);
		alertDialog.setMessage(msg);
		alertDialog.setPositiveButton("Aceptar", okcallback)
		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.cancel();
            }
        });
		alertDialog.show();
	}
	
	public void notification(String titulo, String msg, String okbtn, DialogInterface.OnClickListener okcallback) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(app);
		alertDialog.setTitle(titulo);
		alertDialog.setMessage(msg);
		alertDialog.setPositiveButton(okbtn, okcallback)
		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.cancel();
            }
        });
		alertDialog.show();
	}
	
	public void notification(String titulo, String msg, String okbtn, String cancelbtn,  DialogInterface.OnClickListener okcallback, DialogInterface.OnClickListener cancelcallback) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(app);
		alertDialog.setTitle(titulo);
		alertDialog.setMessage(msg);
		alertDialog.setPositiveButton(okbtn, okcallback)
		.setNegativeButton(cancelbtn, cancelcallback);
		alertDialog.show();
	}
	
	public AlertDialog.Builder input(DialogInterface.OnClickListener callback, String...  data){
		final EditText input = new EditText(app);
		String titulo = data[0],
				btnOk = data[1],
				btnCancel = data[1];
		input.setPadding(10, 10, 10, 10);
        input.setText("");
        AlertDialog.Builder inputDialog =  new AlertDialog.Builder(app)
            .setTitle(titulo)
            .setView(input)
            .setPositiveButton(btnOk,callback)
            .setNegativeButton(btnCancel, new DialogInterface.OnClickListener() {
                @Override
				public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.cancel();
                }
            });
        return inputDialog;
	}
	
	public boolean input(String titulo, String okbtn, final inputDialog callback) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(app);
		final EditText input = new EditText(app);
		alertDialog.setTitle(titulo);
		input.setPadding(10, 10, 10, 10);
        input.setText("");
        alertDialog.setView(input);
		alertDialog.setPositiveButton(okbtn, new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int whichButton) {
            	callback.input(input.getText().toString());
            }
        })
		.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.cancel();
            }
        });
		alertDialog.show();
		return false;
	}
}
