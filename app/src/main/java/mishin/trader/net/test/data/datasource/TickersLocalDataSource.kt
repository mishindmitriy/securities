package mishin.trader.net.test.data.datasource

import mishin.trader.net.test.domain.Ticker

class TickersLocalDataSource {
    fun getTickers(): List<Ticker> {
        return ("SP500.IDX,AAPL.US,RSTI,GAZP,MRKZ,RUAL,HYDR,MRKS,SBER,FEES,TGKA,VTBR,AN H.US," +
                "VICL.US,BURG.US,NBL.US,YETI.US,WSFS.US,NIO.US,DXC.US,MIC.US,HSBC.US,EXPN.EU," +
                "GSK.EU,SHP.EU,MAN.EU,DB1.EU,MUV2.EU,TATE.EU,KGF.EU,MGGT.EU,SG GD.EU")
            .split(",")
            .toList()
            .map { Ticker(it) }
    }
}