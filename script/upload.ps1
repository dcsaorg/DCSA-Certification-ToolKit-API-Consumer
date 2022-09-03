$CurlExecutable = "C:\WINDOWS\system32\curl.exe"
$uri = "http://localhost:9001/v2/uploadShipment"
$files = Get-ChildItem -Recurse -Path $PSScriptRoot -Filter *.json

foreach ($file in $files) {
    $fullPath = $file.FullName;

    $CurlArguments = '--request', 'POST', 
                $uri,
                '--header', "'content-type: multipart/form-data'",
                '--form', "file=@$fullPath",
                '-v';

    & $CurlExecutable @CurlArguments
}