param(
    [Parameter(Mandatory=$True, Position=0, ValueFromPipeline=$false)]
    [System.String]
    $Param1
)

$CurlExecutable = "C:\WINDOWS\system32\curl.exe"
$uri = "http://localhost:9092//conformance/data-management/uploadShipment"
$fullPath = $PSScriptRoot.ToString() + "\" + $Param1.ToString().Trim();

$CurlArguments = '--request', 'POST',
            $uri,
            '--header', "'content-type: multipart/form-data'",
            '--form', "file=@$fullPath",
            '-v';
& $CurlExecutable @CurlArguments

