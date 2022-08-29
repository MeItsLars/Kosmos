module goleveldb

go 1.14

require (
	github.com/fsnotify/fsnotify v1.5.4 // indirect
	github.com/df-mc/goleveldb v1.1.9
	golang.org/x/net v0.0.0-20220607020251-c690dde0001d // indirect
)

replace (
	github.com/df-mc/goleveldb => github.com/stirante/goleveldb v0.0.0-20220829075325-def0525e1126
)