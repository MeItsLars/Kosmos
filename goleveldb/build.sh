# Windows
echo Building for Windows
CGO_ENABLED=1 GOOS=windows GOARCH=amd64 CC=x86_64-w64-mingw32-gcc CXX=x86_64-w64-mingw32-g++ go build -ldflags="-s -w" -o ../src/main/resources/win32-x86_64/goleveldb.dll -buildmode=c-shared ./main.go
# Linux
echo Building for Linux
CGO_ENABLED=1 GOOS=linux GOARCH=amd64 go build -ldflags="-s -w" -o ../src/main/resources/linux-x86_64/goleveldb.so -buildmode=c-shared ./main.go
# MacOS
echo Building for MacOS
CGO_ENABLED=1 GOOS=darwin GOARCH=amd64 CC=o64-clang CXX=o64-clang++ go build -ldflags="-s -w" -o ../src/main/resources/darwin-x86_64/goleveldb.so -buildmode=c-shared ./main.go