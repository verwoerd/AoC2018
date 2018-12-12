package tools

/**
 * @author verwoerd
 * @since 1-12-2018
 */

inline fun timeSolution(body: () -> Unit) {
  val start = System.currentTimeMillis()
  try {
    body()
  } catch (e : FinishedSignal) {
    //Do nothing, cheap escape to get out of a loop
  }catch (e: Throwable) {
    println("Exception caught ${e.localizedMessage}")
    e.printStackTrace()
  } finally {
    val finish = System.currentTimeMillis()
    println("Total runtime: ${finish - start}ms")
  }
}

class FinishedSignal: RuntimeException()
