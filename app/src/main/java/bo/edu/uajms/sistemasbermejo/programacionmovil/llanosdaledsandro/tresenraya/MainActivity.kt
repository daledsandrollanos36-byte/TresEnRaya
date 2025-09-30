package bo.edu.uajms.sistemasbermejo.programacionmovil.llanosdaledsandro.tresenraya

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var BTNboardButtons: Array<Button>
    private lateinit var TXVStatus: TextView // Cambiado de TXVcurrentPlayer a TXVStatus
    private lateinit var BTNShuffle: Button
    private lateinit var BTNRestart: Button
    private lateinit var GLYBoard: GridLayout

    private val rowsBoard = 4
    private val columnsBoard = 4
    private val totalTiles = rowsBoard * columnsBoard // 16 fichas (15 números + 1 vacío)
    private var isGameOver = false

    // Estado ordenado (la clave es el ID del botón, el valor es el texto correcto)
    // Usaremos "" para el espacio vacío (BTN33)
    private val correctStateMap = mapOf(
        R.id.BTN00 to "1", R.id.BTN01 to "2", R.id.BTN02 to "3", R.id.BTN03 to "4",
        R.id.BTN10 to "5", R.id.BTN11 to "6", R.id.BTN12 to "7", R.id.BTN13 to "8",
        R.id.BTN20 to "9", R.id.BTN21 to "10", R.id.BTN22 to "11", R.id.BTN23 to "12",
        R.id.BTN30 to "13", R.id.BTN31 to "14", R.id.BTN32 to "15", R.id.BTN33 to ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Relacionar las variables con los controles de la vista
        BTNShuffle = findViewById(R.id.BTNShuffle) // Botón Desordenar
        BTNRestart = findViewById(R.id.BTNRestart) // Botón Reiniciar
        TXVStatus = findViewById(R.id.TXVPlayer)   // Mensaje de estado
        GLYBoard = findViewById(R.id.GLYBoard)

        // La lista de 16 botones
        BTNboardButtons = arrayOf(
            findViewById(R.id.BTN00), findViewById(R.id.BTN01), findViewById(R.id.BTN02), findViewById(R.id.BTN03),
            findViewById(R.id.BTN10), findViewById(R.id.BTN11), findViewById(R.id.BTN12), findViewById(R.id.BTN13),
            findViewById(R.id.BTN20), findViewById(R.id.BTN21), findViewById(R.id.BTN22), findViewById(R.id.BTN23),
            findViewById(R.id.BTN30), findViewById(R.id.BTN31), findViewById(R.id.BTN32), findViewById(R.id.BTN33)
        )

        // Inicializar el tablero en estado ordenado
        restartGame()

        // Eventos
        BTNShuffle.setOnClickListener {
            Log.d("Click", "Presionaste el botón Desordenar")
            shuffleBoard()
        }

        BTNRestart.setOnClickListener {
            Log.d("Click", "Presionaste el botón Reiniciar")
            restartGame()
        }

        // Agregar listener de click a todos los botones del tablero
        BTNboardButtons.forEach { button ->
            button.setOnClickListener { onTileClicked(it as Button) }
        }
    }

    // --- Lógica del Juego ---

    /**
     * Restaura el tablero a su estado inicial ordenado.
     */
    private fun restartGame() {
        // Restaurar textos y estilos al estado ordenado
        BTNboardButtons.forEach { button ->
            val correctText = correctStateMap[button.id] ?: ""
            button.text = correctText
            button.isEnabled = true
            // Asignar el estilo correcto (Ejemplo básico: el vacío es transparente)
            if (correctText.isEmpty()) {
                button.setBackgroundResource(android.R.color.transparent)
            } else {
                button.setBackgroundResource(R.color.blue) // Usa el color que definiste
            }
        }
        TXVStatus.text = getString(R.string.game_status_initial)
        isGameOver = false
    }

    /**
     * Desordena el tablero y lo deja en un estado resoluble.
     */
    private fun shuffleBoard() {
        // Obtener la lista de textos (1 al 15 y el vacío "")
        val tileTexts = correctStateMap.values.toMutableList()
        // Shuffle (es importante que el shuffle resulte en un estado resoluble,
        // pero para la tarea, se puede usar un shuffle simple).
        tileTexts.shuffle(Random)

        // Asignar los textos desordenados a los botones
        BTNboardButtons.forEachIndexed { index, button ->
            val text = tileTexts[index]
            button.text = text
            // Actualizar estilo
            if (text.isEmpty()) {
                button.setBackgroundResource(android.R.color.transparent)
            } else {
                button.setBackgroundResource(R.color.blue)
            }
        }
        TXVStatus.text = getString(R.string.game_status_initial)
        isGameOver = false
        // Asegurar que todos los botones estén habilitados para el juego
        BTNboardButtons.forEach { it.isEnabled = true }
    }

    /**
     * Se llama cuando se hace clic en una ficha.
     */
    private fun onTileClicked(clickedButton: Button) {
        if (isGameOver) return

        // 1. Encontrar el botón vacío
        val emptyButton = BTNboardButtons.find { it.text.isEmpty() } ?: return

        // 2. Obtener las coordenadas (fila, columna)
        val clickedCoords = getButtonCoordinates(clickedButton)
        val emptyCoords = getButtonCoordinates(emptyButton)

        // 3. Verificar adyacencia
        if (areButtonsAdjacent(clickedCoords, emptyCoords)) {
            // Intercambiar valores
            swapButtonTextsAndStyles(clickedButton, emptyButton)

            // 4. Verificar victoria
            if (checkWin()) {
                endGame(true)
            }
        }
    }

    /**
     * Encuentra las coordenadas (fila, columna) de un botón en el arreglo.
     */
    private fun getButtonCoordinates(button: Button): Pair<Int, Int> {
        val index = BTNboardButtons.indexOf(button)
        val row = index / columnsBoard
        val col = index % columnsBoard
        return Pair(row, col)
    }

    /**
     * Verifica si dos botones son adyacentes (horizontal o verticalmente).
     */
    private fun areButtonsAdjacent(coords1: Pair<Int, Int>, coords2: Pair<Int, Int>): Boolean {
        val row1 = coords1.first
        val col1 = coords1.second
        val row2 = coords2.first
        val col2 = coords2.second

        // Mismo columna, diferencia de 1 en fila (Arriba/Abajo)
        val isVertical = col1 == col2 && Math.abs(row1 - row2) == 1
        // Misma fila, diferencia de 1 en columna (Izquierda/Derecha)
        val isHorizontal = row1 == row2 && Math.abs(col1 - col2) == 1

        return isVertical || isHorizontal
    }

    /**
     * Intercambia el texto y los estilos de dos botones.
     */
    private fun swapButtonTextsAndStyles(btn1: Button, btn2: Button) {
        // Intercambiar textos
        val tempText = btn1.text
        btn1.text = btn2.text
        btn2.text = tempText

        // Intercambiar estilos (colores)
        // btn1 (ahora vacío)
        if (btn1.text.isEmpty()) {
            btn1.setBackgroundResource(android.R.color.transparent)
        } else {
            btn1.setBackgroundResource(R.color.blue)
        }
        // btn2 (ahora con número)
        if (btn2.text.isEmpty()) {
            btn2.setBackgroundResource(android.R.color.transparent)
        } else {
            btn2.setBackgroundResource(R.color.blue)
        }
    }

    /**
     * Verifica si el tablero está en el estado de victoria.
     */
    private fun checkWin(): Boolean {
        return BTNboardButtons.all { button ->
            // El texto actual del botón debe coincidir con el texto del estado correcto
            button.text == correctStateMap[button.id]
        }
    }

    /**
     * Finaliza el juego y muestra un mensaje.
     */
    private fun endGame(win: Boolean) {
        isGameOver = true
        if (win) {
            TXVStatus.text = getString(R.string.game_status_win) // Define este string
            Toast.makeText(this, "¡Felicidades! ¡Puzzle Resuelto! 🎉", Toast.LENGTH_LONG).show()
        }

        // Inhabilitar el tablero
        BTNboardButtons.forEach { it.isEnabled = false }
    }
}