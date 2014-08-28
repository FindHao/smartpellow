package small.find;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class Bluetooth {
	private BluetoothAdapter btAdapter;
	private BluetoothDevice btDevice;
	private BluetoothSocket btsSocket;
	static OutputStream outputStream;
	
	private BroadcastReceiver mReceiver;
	
	private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	//要配对的地址
	String address="";
	
	
	private BroadcastReceiver btConnectReceiver;
	private BroadcastReceiver btDisconnectReceiver;
	private IntentFilter connectIntentFilter;
	private IntentFilter disconnectIntentFilter;
	private boolean isConnect = false;
	
	
	public Bluetooth() {
		btAdapter=BluetoothAdapter.getDefaultAdapter();
		
		Set<BluetoothDevice>devices=btAdapter.getBondedDevices();
		if(devices)
		
	
	}
	
	
	
	
	
	
	
	
	
	
}
