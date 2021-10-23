import cats._
import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.blaze.server._
import org.http4s.dsl.Http4sDsl

object Http4sTutorial extends IOApp {

  def helloWorld[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] { case GET -> Root =>
      Ok("Hello World!")
    }
  }

  def healthProve[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] { case GET -> Root =>
      Ok("Ok!")
    }
  }

  def run(args: List[String]): IO[ExitCode] = {

    val apis = Router(
      "/hello" -> helloWorld[IO],
      "/healthz" -> healthProve[IO]
    ).orNotFound

    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(3000, "0.0.0.0")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
