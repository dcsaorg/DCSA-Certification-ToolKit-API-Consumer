$CurlExecutable = "C:\WINDOWS\system32\curl.exe"
$uri = "http://localhost:9092//conformance/data-management/uploadShipment"
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