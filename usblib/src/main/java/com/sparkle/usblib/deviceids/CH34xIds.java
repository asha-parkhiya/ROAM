package com.sparkle.usblib.deviceids;

import static com.sparkle.usblib.deviceids.Helpers.createDevice;
import static com.sparkle.usblib.deviceids.Helpers.createTable;

public class CH34xIds {
    private static final long[] ch34xDevices = createTable(
            createDevice(0x4348, 0x5523),
            createDevice(0x1a86, 0x7523),
            createDevice(0x1a86, 0x5523),
            createDevice(0x1a86, 0x0445)
    );

    private CH34xIds() {

    }

    public static boolean isDeviceSupported(int vendorId, int productId) {
        return Helpers.exists(ch34xDevices, vendorId, productId);
    }
}
