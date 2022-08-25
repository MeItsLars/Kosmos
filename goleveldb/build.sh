# Windows
echo Building for Windows
CGO_ENABLED=1 GOOS=windows GOARCH=amd64 CC="zig cc -target x86_64-windows-gnu" CXX="zig c++ -target x86_64-windows-gnu"  go build -ldflags="-s -w" -o windows-adm64/goleveldb.dll -buildmode=c-shared ./main.go
# Linux
echo Building for Linux
CGO_ENABLED=1 GOOS=linux GOARCH=amd64 CC="zig cc -target x86_64-linux" CXX="zig c++ -target x86_64-linux"  go build -ldflags="-s -w" -o linux-amd64/goleveldb.so -buildmode=c-shared ./main.go
# MacOS
echo Building for MacOS
CGO_ENABLED=1 GOOS=darwin GOARCH=amd64 CC="zig cc -target x86_64-macos-gnu" CXX="zig c++ -target x86_64-macos-gnu"  go build -ldflags="-s -w" -o darwin-amd64/goleveldb.so -buildmode=c-shared ./main.go
CGO_ENABLED=1 GOOS=darwin GOARCH=arm64 CC="zig cc -target aarch64-macos-gnu" CXX="zig c++ -target aarch64-macos-gnu"  go build -ldflags="-s -w" -o darwin-arm64/goleveldb.so -buildmode=c-shared ./main.go