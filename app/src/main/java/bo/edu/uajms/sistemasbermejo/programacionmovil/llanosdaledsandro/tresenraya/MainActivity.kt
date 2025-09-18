package bo.edu.uajms.sistemasbermejo.programacionmovil.llanosdaledsandro.tresenraya

import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    //Declaracion de las variables
    private lateinit var BTNboardButtons: Array<Button>
    private lateinit var TXVcurrentPlayer: TextView
    private lateinit var BTNRestartGame: Button
    private val rowsBoard = 3
    private val columnsBoard = 3
    private var currentPlayer = 1
    private lateinit var board: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Relacionar las variables con los controles de la vista
        BTNRestartGame = findViewById(R.id.BTNRestartgame)
        TXVcurrentPlayer = findViewById(R.id.TXVPlayer)
        BTNboardButtons = arrayOf(
            findViewById(R.id.BTN00),
            findViewById(R.id.BTN01),
            findViewById(R.id.BTN02),
            findViewById(R.id.BTN10),
            findViewById(R.id.BTN11),
            findViewById(R.id.BTN12),
            findViewById(R.id.BTN20),
            findViewById(R.id.BTN21),
            findViewById(R.id.BTN22)
        )
        //Limpiar o Inicializar controles
        board = Array(rowsBoard) { Array(columnsBoard) { "" } }
        boardClear()
        //Eventos
        BTNRestartGame.isVisible = true;
        BTNRestartGame.setOnClickListener() {
            Log.e("Clic", "Presionaste el boton Reiniciar")
            boardClear();
            enableBoard()
        }
        for (i in BTNboardButtons.indices) {
            var rowTablero: Int = i / 3
            var colTablero: Int = i % 3
            BTNboardButtons[i].setOnClickListener() {
                Log.e("Clic", "Boton del Tablero row ${rowTablero} col ${colTablero}")
                if (BTNboardButtons[i].text == "") {
                    if (currentPlayer == 1) {
                        BTNboardButtons[i].text = "X"
                        board[rowTablero][colTablero] = "X"
                        TXVcurrentPlayer.text = getString(R.string.playerO)
                        currentPlayer = 2
                    } else {
                        BTNboardButtons[i].text = "O"
                        board[rowTablero][colTablero] = "O"
                        TXVcurrentPlayer.text = getString(R.string.playerX)
                        currentPlayer = 1;
                    }

                    var winner = winnerIdentify()
                    if (winner != "") {
                        if (winner == "X") {
                            TXVcurrentPlayer.text = getString(R.string.winplayerX)
                            disableBoard()
                        }
                        if (winner == "O") {
                            TXVcurrentPlayer.text = getString(R.string.winplayerO)
                            disableBoard()
                        }

                    }
                    else if(tieDetect())
                    {
                        TXVcurrentPlayer.text = getString(R.string.tie)
                        disableBoard()

                    }
                }
            }

        }
    }

    fun boardClear() {
        for (i in BTNboardButtons.indices) {
            BTNboardButtons[i].text = "";
        }
        currentPlayer = 1;
        TXVcurrentPlayer.text = getString(R.string.playerX)
        for (i in 0..rowsBoard - 1) {
            for (j in 0..columnsBoard - 1) {
                board[i][j] = ""
            }
        }
    }

    fun winnerIdentify(): String {
        var winner = false
        //Ganador en las filas
        for (i in 0..rowsBoard - 1) {
            if (board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] != "") {
                return board[i][0]
            }
        }
        //Ganador en las columnas
        for (j in 0..columnsBoard - 1) {
            if (board[0][j] == board[1][j] && board[0][j] == board[2][j] && board[0][j] != "") {
                return board[0][j]
            }
        }
        //Ganador diagonal principal
        if (board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] != "") {
            return board[0][0]
        }
        //Ganador diagonal secundaria
        if (board[2][0] == board[1][1] && board[0][2] == board[1][1] && board[1][1] != "") {
            return board[1][1]
        }
        return ""
    }

    fun disableBoard() {
        for (i in BTNboardButtons.indices) {
            BTNboardButtons[i].isEnabled = false
        }
    }

    fun enableBoard() {
        for (i in BTNboardButtons.indices) {
            BTNboardButtons[i].isEnabled = true
        }
    }

    fun tieDetect(): Boolean {
        for (i in BTNboardButtons.indices) {

           if (BTNboardButtons[i].text == "")
            {
            return false
            }
        }
        return true
    }
}









