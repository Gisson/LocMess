package main


import(
  "fmt"
  "net/http"
  "strings"
  "log"
  "encoding/base64"
  "crypto/sha256"
  "crypto/hmac"
)

type token struct{
  sessid string //Base64 encoded session id
  expiration time.Time //Expiration date
}

type User struct{
  name string
  password string
  sessid token
}

func registerUser(name, password string) User{
  newuser := User{ name: name, password: password}
}

func sayhelloName(w http.ResponseWriter, r *http.Request) {
    r.ParseForm()  // parse arguments, you have to call this by yourself
    fmt.Println(r.Form)  // print form information in server side
    fmt.Println("path", r.URL.Path)
    fmt.Println("scheme", r.URL.Scheme)
    fmt.Println(r.Form["url_long"])
    for k, v := range r.Form {
        fmt.Println("key:", k)
        fmt.Println("val:", strings.Join(v, ""))
    }
    fmt.Fprintf(w, "Hello astaxie!") // send data to client side
}

func main() {
    http.HandleFunc("/", sayhelloName) // set router
    err := http.ListenAndServe(":9090", nil) // set listen port
    if err != nil {
        log.Fatal("ListenAndServe: ", err)
    }
}
